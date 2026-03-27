# meta-xcpng

Status: under construction

This is a BitBake layer to build XCP-ng 9.x.  Its role is to collect
all informations necessary to build XCP-ng, from individual RPMs to
DNF repositories and insallation ISO.

## maintenance

To mirror the DNF state of Almalinux 10.0 into `meta-almalinux`, use
(adapted from `dnf-bridge/README.md`):

```
podman run --rm --platform linux/amd64/v2 -it \
    -v $PWD:/xcpng \
    ghcr.io/almalinux/10-base:10 \
    sh -c 'dnf install -y git && /xcpng/dnf-bridge/scripts/gen-dnf-proxy.py /xcpng/meta-almalinux'
```
