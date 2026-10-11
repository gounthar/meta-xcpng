#!/usr/bin/python3
"""Wrap a riscv64 binary in a U-Boot legacy image header, as
`mkimage -A riscv -O linux -T kernel -C none -a ADDR -e ADDR -n NAME`
does, so U-Boot's bootm can start Xen. Header layout: struct
legacy_img_hdr in U-Boot's include/image.h. The timestamp comes from
SOURCE_DATE_EPOCH, as in mkimage, so the output is reproducible."""

import argparse
import os
import struct
import time
import zlib

IH_MAGIC = 0x27051956
IH_OS_LINUX = 5
IH_ARCH_RISCV = 26
IH_TYPE_KERNEL = 2
IH_COMP_NONE = 0
HDR = ">IIIIIIIBBBB32s"


def main():
    p = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    p.add_argument(
        "-a",
        "--addr",
        type=lambda s: int(s, 0),
        required=True,
        help="load address, also used as the entry point",
    )
    p.add_argument("-n", "--name", default="", help="image name, 31 bytes max")
    p.add_argument("input")
    p.add_argument("output")
    a = p.parse_args()

    name = a.name.encode()
    if len(name) > 31:
        p.error("name longer than 31 bytes")
    with open(a.input, "rb") as f:
        data = f.read()
    stamp = int(os.environ.get("SOURCE_DATE_EPOCH", time.time()))
    fields = [
        IH_MAGIC,
        0,
        stamp,
        len(data),
        a.addr,
        a.addr,
        zlib.crc32(data),
        IH_OS_LINUX,
        IH_ARCH_RISCV,
        IH_TYPE_KERNEL,
        IH_COMP_NONE,
        name,
    ]
    fields[1] = zlib.crc32(struct.pack(HDR, *fields))
    with open(a.output, "wb") as f:
        f.write(struct.pack(HDR, *fields) + data)


if __name__ == "__main__":
    main()
