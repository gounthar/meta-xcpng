inherit srpm-intree

# do not pull xcp-ng-release
PACKAGE_NEEDS_BOOTSTRAP = "1"

RDEPENDS:${PN} = " \
python3 \
"
