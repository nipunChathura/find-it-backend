# Remove Firebase JSON from Git History (fix push rejected)

GitHub blocks the push because the Firebase service account JSON (a secret) is still in **commit e30eb06**. You must remove that file from history.

## Option 1: Interactive rebase (recommended)

Run these in the project root (`E:\ICBT\Assignment\Final\Find It\Project\backend\findit`):

```bash
# 1. Start interactive rebase before the commit that added the file
git rebase -i e30eb06^

# 2. In the editor: change "pick" to "edit" for commit e30eb06, save and close.

# 3. When rebase stops at that commit, remove the file from the commit and continue
git rm --cached src/main/resources/find-it-8283f-firebase-adminsdk-fbsvc-44f6d583fc.json
git commit --amend --no-edit
git rebase --continue

# 4. If conflicts appear, resolve them and run: git rebase --continue

# 5. Push (history changed, so force push is needed)
git push origin feature/merchant-registration --force-with-lease
```

## Option 2: Filter the file from entire branch

If you prefer to remove the file from every commit on the branch:

```bash
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch src/main/resources/find-it-8283f-firebase-adminsdk-fbsvc-44f6d583fc.json" --prune-empty -- feature/merchant-registration
git push origin feature/merchant-registration --force-with-lease
```

## After fixing

- Keep the JSON file only **on your machine** (e.g. in `src/main/resources/`) and **out of Git** (it’s in `.gitignore`).
- For CI/deploy, use environment variables or a secret store, not a committed file.
