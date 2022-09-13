import datetime as dt
import os

# Settings
build_path="./build/libs/"
mod_name = "lootbeams"
mc_version = "1.19.2"
build = "release"
# ====

date = dt.datetime.now()
day = date.strftime("%d")
months_abbr = ["jan", "feb", "mar", "apr", "may", "june", "july", "aug", "sept", "oct", "nov", "dec"]
month = str(months_abbr[int(date.strftime("%m")) - 1])
year = date.strftime("%y")

file_name = str.format("{0}-{1}-{2}.jar", mod_name, mc_version, build)
version_name = str.format("{0}-{1}-{2}-{3}{4}{5}.jar", mod_name, mc_version, build, month, day, year)

os.rename(build_path + file_name, build_path + version_name)