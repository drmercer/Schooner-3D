# If stdout has been modified, this script closes
# the modified stdout and resets it to the regular
# output stream. This is very useful if export.py
# crashes and leaves the log file stream open.

import sys

if sys.stdout != sys.__stdout__:
    sys.stdout.close()
    sys.stdout = sys.__stdout__