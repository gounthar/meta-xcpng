inherit rpm

REPONAME ?= "${PN}"

SRC_URI = "git://github.com/xcp-ng-rpms/${REPONAME};protocol=https;nobranch=1"
S = "${UNPACKDIR}/git"
