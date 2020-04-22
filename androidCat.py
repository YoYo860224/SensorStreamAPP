from subprocess import Popen, PIPE

while(True):
    with Popen(['adb shell cat /storage/emulated/0/sensor.txt'], shell=True, stdout=PIPE) as proc:
        string = proc.stdout.readline().decode('UTF-8')
        try:
            speed, zaxis = string.split(' ')
            print("======================")
            print("Speed: ", "{: .4f}".format(float(speed)), " m/s")
            print("Zaxis: ", "{: .4f}".format(float(zaxis)), " m/s")
        except Exception as identifier:
            print("No value")