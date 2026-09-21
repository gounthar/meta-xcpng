#! /bin/sh
set -e

git submodule foreach 'git fetch && git status | grep --color -E "Your branch .*|rebase in progress" || true'
