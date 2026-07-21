#! /bin/sh
set -e

git submodule foreach 'git fetch && git status | grep --color "Your branch .*" || true'
