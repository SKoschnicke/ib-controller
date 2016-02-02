FROM java:7
MAINTAINER Sven Koschnicke <sven@koschnicke.de>

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update
RUN apt-get -y install ant xfonts-base vnc4server i3 xterm

# Startup of IBController fails with error
# "GLib-GIO-ERROR **: No GSettings schemas are installed on the system"
# when the following package is not installed:
RUN apt-get -y install gsettings-desktop-schemas

# add a password file so vnc does not ask for it on startup (contains the encrypted string "password", but will not be asked on connection)
ADD vnc_password_file /root/.vnc/passwd

# expose VNC port
# WARNING: ensure to firewall this port to deny access from outside
# if docker doesn't run on your local machine, you can tunnel the VNC-connection
# with SSH:
# > ssh <remote-machine> -N -L 10000:localhost:<exposed docker port>
# > vncviewer localhost:10000
EXPOSE 5902

ENV DISPLAY :2

VOLUME /usr/src/ib-controller

CMD Xvnc :2 -geometry 1920x1080 -SecurityTypes None
