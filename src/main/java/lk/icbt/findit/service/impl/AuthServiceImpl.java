package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.LogSupport;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.AuthDto;
import lk.icbt.findit.entity.*;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerRepository;
import lk.icbt.findit.repository.MerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.service.AuthService;
import lk.icbt.findit.util.JwtUtil;
import lk.icbt.findit.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Value("${username.policy.regex}")
    private String usernameRegex;

    @Value("${password.policy.regex}")
    private String passwordRegex;

    @Value("${email.validation.regex}")
    private String emailRegex;

    @Value("${phone.number.validation.regex}")
    private String phoneNumberRegex;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    @Override
    public AuthDto register(AuthDto authDto) {
        log.info(LogSupport.USER_REGISTER + "starting.", authDto.getUsername(), authDto.getRegistrationType());

        if (authDto.getRegistrationType() == null || authDto.getRegistrationType().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "registrationType is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "registrationType is required");
        }

        String registrationType = authDto.getRegistrationType();
        if (registrationType.equalsIgnoreCase(Constants.CUSTOMER_REGISTRATION_TYPE)) {
            authDto = customerRegistration(authDto);
        } else if (registrationType.equalsIgnoreCase(Constants.MERCHANT_REGISTRATION_TYPE)) {
            authDto  = merchantRegistration(authDto);
        } else {
            log.error(LogSupport.USER_REGISTER + "registrationType is invalid.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.INVALID_REGISTRATION_TYPE_CODE, "registrationType is invalid");
        }

        log.info(LogSupport.USER_REGISTER + "end.", authDto.getUsername(), authDto.getRegistrationType());
        return authDto;
    }

    private AuthDto customerRegistration(AuthDto authDto) {

        if (authDto.getEmail() == null || authDto.getEmail().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "email is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "email is required");
        }

        if (!authDto.getEmail().matches(emailRegex)) {
            log.error(LogSupport.USER_REGISTER + "email is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.EMAIL_VALIDATION_ERROR_CODE, "email is validation Failed");
        }

        if (authDto.getPhoneNumber() == null || authDto.getPhoneNumber().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "phoneNumber is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "phoneNumber is required");
        }

        if (!authDto.getPhoneNumber().matches(phoneNumberRegex)) {
            log.error(LogSupport.USER_REGISTER + "phoneNumber is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.PHONE_NUMBER_VALIDATION_ERROR_CODE, "phoneNumber is validation Failed");
        }

        Customer byEmail = customerRepository.findByEmail(authDto.getEmail());
        if (byEmail != null) {
            log.error(LogSupport.USER_REGISTER + "customer email already exists.", authDto.getUsername(),  authDto.getRegistrationType());
            authDto.setStatus(ResponseStatus.FAILURE.getStatus());
            authDto.setResponseCode(ResponseCodes.FAILED_CODE);
            authDto.setResponseMessage("Customer email already exists");
            return authDto;
        }

        Customer customer = new Customer();
        customer.setCustomerName(authDto.getFirstName() + " " + authDto.getLastName());
        customer.setEmail(authDto.getEmail());
        customer.setPhoneNumber(authDto.getPhoneNumber());
        customer.setMembershipType(MembershipType.SILVER);
        customer.setStatus(Constants.CUSTOMER_ACTIVE_STATUS);
        customer.setPicture(authDto.getPicture());
        customer.setCreatedDatetime(Utils.getCurrentDateByTimeZone(Constants.TIME_ZONE));
        customerRepository.save(customer);

        authDto.setCustomerId(customer.getCustomerId());
        authDto.setStatus(ResponseStatus.SUCCESS.getStatus());
        authDto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        authDto.setResponseMessage("Successfully created customer with email: " + authDto.getEmail());
        return authDto;
    }

    private AuthDto merchantRegistration(AuthDto authDto) {
        if (authDto.getUsername() == null || authDto.getUsername().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "username is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "username is required");
        }

        if (!authDto.getUsername().matches(usernameRegex)) {
            log.error(LogSupport.USER_REGISTER + "username is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.USERNAME_VALIDATION_ERROR_CODE, "username is validation Failed");
        }

        if (authDto.getPassword() == null || authDto.getPassword().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "password is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "password is required");
        }

        if (!authDto.getPassword().matches(passwordRegex)) {
            log.error(LogSupport.USER_REGISTER + "password is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.PASSWORD_VALIDATION_ERROR_CODE, "password is validation Failed");
        }

        if (authDto.getEmail() == null || authDto.getEmail().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "email is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "email is required");
        }

        if (!authDto.getEmail().matches(emailRegex)) {
            log.error(LogSupport.USER_REGISTER + "email is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.EMAIL_VALIDATION_ERROR_CODE, "email is validation Failed");
        }

        if (authDto.getPhoneNumber() == null || authDto.getPhoneNumber().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "phoneNumber is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "phoneNumber is required");
        }

        if (!authDto.getPhoneNumber().matches(phoneNumberRegex)) {
            log.error(LogSupport.USER_REGISTER + "phoneNumber is validation Failed.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.PHONE_NUMBER_VALIDATION_ERROR_CODE, "phoneNumber is validation Failed");
        }

        if (authDto.getAddress() == null || authDto.getAddress().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "address is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "address is required");
        }

        if (authDto.getFirstName() == null || authDto.getFirstName().isEmpty()) {
            log.error(LogSupport.USER_REGISTER + "firstName is required.", authDto.getUsername(),  authDto.getRegistrationType());
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "firstName is required");
        }

        Merchant byEmail = merchantRepository.findByMerchantEmail(authDto.getEmail());
        if (byEmail != null) {
            log.error(LogSupport.USER_REGISTER + "merchant email already exists.", authDto.getUsername(),  authDto.getRegistrationType());
            authDto.setStatus(ResponseStatus.FAILURE.getStatus());
            authDto.setResponseCode(ResponseCodes.FAILED_CODE);
            authDto.setResponseMessage("Merchant email already exists");
            return authDto;
        }

        Optional<User> byUsername = userRepository.findByUsername(authDto.getUsername());
        if (byUsername.isPresent()) {
            log.error(LogSupport.USER_REGISTER + "merchant username already exists.", authDto.getUsername(),  authDto.getRegistrationType());
            authDto.setStatus(ResponseStatus.FAILURE.getStatus());
            authDto.setResponseCode(ResponseCodes.FAILED_CODE);
            authDto.setResponseMessage("Merchant username already exists");
            return authDto;
        }

        User user = new  User();
        user.setUsername(authDto.getUsername());
        user.setPassword(passwordEncoder.encode(authDto.getPassword()));
        user.setRole(Role.MERCHANT);
        user.setStatus(Constants.USER_ACTIVE_STATUS);
        user.setIsSystemUser(Constants.DB_FALSE);

        userRepository.save(user);

        Merchant merchant = getMerchant(authDto, user);

        merchantRepository.save(merchant);

        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        ));

        authDto.setToken(token);
        authDto.setRole(user.getRole().name());
        authDto.setUserId(user.getUserId());
        authDto.setMerchantId(merchant.getMerchantId());
        authDto.setStatus(ResponseStatus.SUCCESS.getStatus());
        authDto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        authDto.setResponseMessage("Successfully created merchant with email: " + authDto.getEmail());
        return authDto;
    }

    private static Merchant getMerchant(AuthDto authDto, User user) {
        Merchant merchant = new Merchant();
        merchant.setMerchantName(authDto.getFirstName() + " "  + authDto.getLastName());
        merchant.setMerchantEmail(authDto.getEmail());
        merchant.setMerchantAddress(authDto.getAddress());
        merchant.setMerchantPhoneNumber(authDto.getPhoneNumber());
        merchant.setUser(user);
        merchant.setMerchantType(MerchantType.FREE);
        merchant.setStatus(Constants.MERCHANT_ACTIVE_STATUS);
        merchant.setPicture(authDto.getPicture());
        return merchant;
    }

    @Override
    public AuthDto login(AuthDto authDto) {
        return null;
    }
}
