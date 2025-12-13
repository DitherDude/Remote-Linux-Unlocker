# Remote-Linux-Unlocker

Remote Linux Unlocker is an android application paired with a linux daemon that allows users to unlock and lock the Ubuntu Unity lock screen.

## How to install the android application

Install the android application from the zip file (download command below) or download the source from this repository.

## How to install the linux daemon

```console
user@machine:~/Downloads$ wget https://github.com/maxchehab/remote-linux-unlocker/raw/master/linux-daemon/linux-daemon.zip
user@machine:~/Downloads$ unzip linux-daemon.zip
user@machine:~/Downloads$ cd linux-daemon
#edit `unlocker-daemon.service` so that the absolute path to unlocker-daemon.py is correct
#run the below commands as sudo!
root@machine:.../linux-daemon# cp unlocker-daemon.service /etc/systemd/system/unlocker-daemon.service
root@machine:~# systemctl daemon-reload
root@machine:~# systemctl enable unlocker-daemon
root@machine:~# systemctl start unlocker-daemon

user@machine:.../linux-daemon$ ./remote-linux-pair
```
