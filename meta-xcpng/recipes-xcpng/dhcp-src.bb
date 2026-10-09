# ISC dhclient from EPEL 10's source RPM (dhcp-4.4.3-23.el10_1.src.rpm). No EL10 repository
# ships dhclient, and xcp-networkd runs /sbin/dhclient for a DHCP management PIF
# (xapi-project/xen-api#6980). Changes to the spec, server still off:
# - the RHEL >= 10 branch says %bcond_without dhclient, so dhcp-client is built;
# - _module_build is set, which skips the doxygen API docs (doxygen needs graphviz);
# - dhclient6.conf.example is also copied when only the client is built (Fedora copies it
#   under the dhcpd conditional, so client-without-server fails in %files);
# - for srpm-intree: "BuildRequires: systemd systemd-devel" split in two (the class splits
#   on commas), pkgconfig(libsystemd) dropped (no such virtual in the bridge; systemd-devel
#   ships libsystemd.pc), and a changelog line no longer says %%define (the class expands
#   the spec twice, and the second pass reads it as a macro definition).
inherit srpm-intree
SRPM_NAME = "dhcp"
PACKAGES = ""
# The spec declares these unconditionally but only ships them with the server
# (and there is no base dhcp package), so nothing would satisfy their checkinstall.
PACKAGES:remove = "dhcp-libs-static dhcp-devel dhcp-devel-doc"
