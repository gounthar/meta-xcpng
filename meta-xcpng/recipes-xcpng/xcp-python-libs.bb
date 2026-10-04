# xcp-python-libs from the XCP-ng RPM repository (xcp-ng-rpms/xcp-python-libs,
# 8aaa32b), adapted for riscv64: xapi-core requires python3-xcp-libs.
inherit srpm-intree
SRPM_NAME = "xcp-python-libs"
PACKAGES = ""
RDEPENDS:python3-xcp-libs = "python3-six"
