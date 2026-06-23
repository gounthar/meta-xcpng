inherit xcp-ng-rpm

SRCREV = "6da933cffbb62d14c5f81e1c296ba583137138c3"

# FIXME: below should be automatically extracted

PV = "1.1.1"
PR = "2"

DEPENDS = " \
rpm/json-c-devel \
"

PACKAGES += "${PN}-devel"

RDEPENDS:${PN}-devel = "${PN}"
