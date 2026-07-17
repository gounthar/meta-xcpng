inherit xcp-ng-rpm

SRCREV = "33fa214194831220521d672aa7c153c92aa046ca"

# do not pull xcp-ng-release
PACKAGE_NEEDS_BOOTSTRAP = "1"

# FIXME: below should be automatically extracted

PV = "8.99.0"
PR = "1"

DEPENDS = " \
rpm/systemd \
rpm/branding-xcp-ng \
rpm/python3-devel \
rpm/python3-rpm-macros \
"

PACKAGES += " \
xcp-ng-repos \
xcp-ng-gpg-keys \
"
