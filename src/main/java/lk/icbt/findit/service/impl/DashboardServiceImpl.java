package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.repository.*;
import lk.icbt.findit.response.ActivityMonthItem;
import lk.icbt.findit.response.DashboardActivityResponse;
import lk.icbt.findit.response.DashboardMonthlyIncomeResponse;
import lk.icbt.findit.response.DashboardSummaryResponse;
import lk.icbt.findit.response.MerchantSummaryData;
import lk.icbt.findit.response.MerchantSummaryResponse;
import lk.icbt.findit.response.MonthlyIncomeItem;
import lk.icbt.findit.response.OutletDistributionItem;
import lk.icbt.findit.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final SubMerchantRepository subMerchantRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final OutletRepository outletRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardSummaryResponse getSummary() {
//        long users = userRepository.countByStatusNot(Constants.USER_DELETED_STATUS);
        ArrayList<String> statuses = new ArrayList<>();
        statuses.add(Constants.USER_DELETED_STATUS);
        ArrayList<Role> userRole = new ArrayList<>();
        userRole.add(Role.USER);
        userRole.add(Role.ADMIN);
        userRole.add(Role.SYSADMIN);
        long users = userRepository.countByStatusNotInAndRoleIn(statuses, userRole);
        long merchants = merchantRepository.countByStatusNot(Constants.MERCHANT_DELETED_STATUS);
        long subMerchants = subMerchantRepository.countByStatusNot(Constants.MERCHANT_DELETED_STATUS);
        long items = itemRepository.countByStatusNot(Constants.ITEM_DELETED_STATUS);
        long customers = customerRepository.count();
        long outlets = outletRepository.count();
        long categories = categoryRepository.countByStatusNot("DELETED");
        long pendingApprovals = userRepository.countByStatus(Constants.USER_PENDING_STATUS)
                + merchantRepository.countByStatus(Constants.MERCHANT_PENDING_STATUS)
                + subMerchantRepository.countByStatus(Constants.MERCHANT_PENDING_STATUS)
                + outletRepository.countByStatus(Constants.OUTLET_PENDING_STATUS);
        long activeDiscounts = discountRepository.countByStatus(Constants.DISCOUNT_ACTIVE_STATUS);

        DashboardSummaryResponse r = new DashboardSummaryResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setUsers(users);
        r.setMerchants(merchants);
        r.setSubMerchants(subMerchants);
        r.setItems(items);
        r.setCustomers(customers);
        r.setOutlets(outlets);
        r.setCategories(categories);
        r.setPendingApprovals(pendingApprovals);
        r.setActiveDiscounts(activeDiscounts);
        return r;
    }

    @Override
    public MerchantSummaryResponse getMerchantSummary() {
        long totalMerchants = merchantRepository.countByStatus(Constants.MERCHANT_ACTIVE_STATUS)
                + merchantRepository.countByStatus(Constants.MERCHANT_INACTIVE_STATUS)
                + merchantRepository.countByStatus(Constants.MERCHANT_PENDING_STATUS);
        long totalOutlets = outletRepository.count();

        List<Object[]> categoryCounts = outletRepository.countOutletsByBusinessCategory();
        List<OutletDistributionItem> outletDistribution = new ArrayList<>();
        for (Object[] row : categoryCounts) {
            BusinessCategory category = row[0] == null ? null : (BusinessCategory) row[0];
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            String label = formatBusinessCategoryLabel(category);
            outletDistribution.add(new OutletDistributionItem(label, count));
        }

        MerchantSummaryData data = new MerchantSummaryData();
        data.setTotalMerchants(totalMerchants);
        data.setTotalOutlets(totalOutlets);
        data.setOutletDistribution(outletDistribution);

        MerchantSummaryResponse r = new MerchantSummaryResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setTotalMerchants(totalMerchants);
        r.setTotalOutlets(totalOutlets);
        r.setOutletDistribution(outletDistribution);
        r.setData(data);
        return r;
    }

    @Override
    public DashboardActivityResponse getActivity(int months) {
        if (months <= 0) months = 6;
        if (months > 24) months = 24;

        List<ActivityMonthItem> activity = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        for (int i = 0; i < months; i++) {
            int monthsAgo = months - 1 - i;
            Calendar cal = (Calendar) now.clone();
            cal.add(Calendar.MONTH, -monthsAgo);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date start = cal.getTime();

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date end = cal.getTime();
            if (monthsAgo == 0) {
                end = new Date();
            }

            String monthLabel = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            long users = userRepository.countByCreatedDatetimeBetween(start, end);
            long merchants = merchantRepository.countByCreatedDatetimeBetween(start, end);
            long outlets = outletRepository.countByCreatedDatetimeBetween(start, end);
            long customers = customerRepository.countByCreatedDatetimeBetween(start, end);
            long payments = paymentRepository.countByCreatedDatetimeBetween(start, end);

            activity.add(new ActivityMonthItem(monthLabel, users, merchants, outlets, customers, payments));
        }

        DashboardActivityResponse r = new DashboardActivityResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setMonths(months);
        r.setActivity(activity);
        return r;
    }

    @Override
    public DashboardMonthlyIncomeResponse getMonthlyIncome(int months) {
        if (months <= 0) months = 12;
        if (months > 24) months = 24;

        List<MonthlyIncomeItem> incomeData = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        for (int i = 0; i < months; i++) {
            int monthsAgo = months - 1 - i;
            Calendar cal = (Calendar) now.clone();
            cal.add(Calendar.MONTH, -monthsAgo);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date start = cal.getTime();

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date end = cal.getTime();
            if (monthsAgo == 0) {
                end = new Date();
            }

            String monthLabel = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            BigDecimal income = paymentRepository.sumAmountByCreatedDatetimeBetween(start, end);
            if (income == null) income = BigDecimal.ZERO;
            incomeData.add(new MonthlyIncomeItem(monthLabel, income));
        }

        DashboardMonthlyIncomeResponse r = new DashboardMonthlyIncomeResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setMonths(months);
        r.setIncomeData(incomeData);
        return r;
    }

    private static String formatBusinessCategoryLabel(BusinessCategory category) {
        if (category == null) return "Other";
        String name = category.name().replace("_", " ").toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : name;
    }
}
