# meta-xcpng

Status: under construction

This is a BitBake layer to build XCP-ng 9.x.  Its role is to collect
all informations necessary to build XCP-ng, from individual RPMs to
DNF repositories and installation ISO.

It uses [dnf-bridge](https://github.com/xcp-ng/dnf-bridge) to sync the
[meta-almalinux](https://github.com/xcp-ng/meta-almalinux) layer
against the AlmaLinux and EPEL DNF repositories, and to provide the
guts powering the recipes in the `meta-xcpng` layer included here.

## usage

### getting the source

Clone the repository, not omitting submodules:
```
src$ git clone --recurse-submodules -b ydi/meta-xcpng https://github.com/xcp-ng/meta-xcpng
src$ cd meta-xcp-ng
```

After switching branch, or updating to be sure not to miss any submodule:
```
src/meta-xcpng$ git submodule update --init --recursive
```

### building

Setup build environment (`./build/` by default, you can decide for a different one):
```
src/meta-xcpng$ . xcpng-init-build-env
```

You may want to check `conf/local.conf` for things to tune (it
defaults to building for `x86_64_v2`.  Useful ones include:

* `MACHINE` at the top, which should be consistent with your host, as
  there is no cross-compilation today.  You need any `x86_64*` host to
  build for any `x86_64*` (exact match is not needed), and an
  `aarch64` one to build for `aarch64`

* `XCPNGDEV_BUILD_OPTS` at the bottom, if you want to activate
  `ccache`

Build a single package (and its build-dependencies, recursively):
```
src/meta-xcpng/build$ bitbake xen
```

## maintenance

To mirror the DNF state of Almalinux 10.0 into `meta-almalinux`, use
(adapted from `dnf-bridge/README.md`):

```
podman run --rm --platform linux/amd64/v2 -it \
    -v $PWD:/xcpng \
    ghcr.io/almalinux/10-base:10 \
    sh -c 'dnf install -y git && /xcpng/dnf-bridge/scripts/gen-dnf-proxy.py /xcpng/meta-almalinux'
```
