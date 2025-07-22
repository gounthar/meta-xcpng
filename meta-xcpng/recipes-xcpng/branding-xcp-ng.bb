inherit xcp-ng-rpm

SRCREV = "6ce3226d83550bcecf24e57335e6cb3f378d64c1"

# do not pull xcp-ng-release
PACKAGE_NEEDS_BOOTSTRAP = "1"

# FIXME: below should be automatically extracted

PV = "8.99.0"
PR = "1.219ab5b"

DEPENDS = " \
rpm/python3-devel \
"

RDEPENDS:${PN} = " \
python3 \
"

# FIXME: generate from PACKAGES
PROVIDES = "rpm/${PN}"
