# D-PAD Tools (npm package)

This repository contains a small example npm package and a GitHub Actions workflow to publish it to npmjs.org when a git tag matching v* is pushed.

To publish:

1. Create an npm token with `npm token create` or from https://www.npmjs.com/settings/<your-username>/tokens
2. Add the token as a repository secret named `NPM_TOKEN` in GitHub settings
3. Create an annotated git tag `git tag -a v1.0.0 -m "release v1.0.0"` and push it `git push origin v1.0.0`
