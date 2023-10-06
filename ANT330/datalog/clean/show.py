import json

def process_file(file_name):
    file = open(file_name, "r")
    lines = file.readlines()
    
    ts = 0
    lateness = 0
    for line in lines:
        r = json.loads(line)
        nts = r["Timestamp"]
        if ts < nts:
            ts = nts
        elif (ts - nts) > lateness:
            lateness = ts - nts
    print(f"lateness ({file_name}): {lateness / 1000 / 60} minutes")

def main():
    process_file("laps.json")
    process_file("telemetry.json")
    process_file("turns.json")

main()