CMPSCI220 Course Materials
==========================

This repository holds all course materials for CMPSCI220. The root directory
holds a [Vagrant] environemnt that you should use for any course development,
from updating the website to hacking on code.

The Vagrant environment has the same GUI (Lubuntu), JVM, and Scala that students
use in the course, but excludes utilities such as text editors. The Vagrant
environment mounts this repository at `/home/vagrant/src`. So, you can use a
text editor on the host to edit files.

## Setup


    host$ vagrant up --provider virtualbox

This will take several minutes. If the `Vagrantfile` changes, you can either
manually apply the update or run

    host$ vagrant destroy

and start again.

## Building

There are two ways to build the system:

- `sbt compileLib` builds the library and `scala220`executable so that they
  can be used locally. But, Docker-based testing does not work.

- `sbt compileDocker` builds the library and testing system. This creates
  fat JARs and the Docker sandbox, which can take some time. This mode is
  almost identical to the setup that students have.


## Releasing

### Preliminaries

1. Have a public/secret keypair in the `gnupg` directory that is recognized
   by the Launchpad CS220 PPA:

       https://launchpad.net/~arjun-guha/+archive/ubuntu/umass-cs220


2. Login to the Docker registry (`vm$ docker login`) and have permission to
   update to this image:

       https://registry.hub.docker.com/u/arjunguha/cs220/

### Release Procedure

1. Add a new entry at the top of `ppa/debian/changelog`. You *have* to:

   a.  Increment the version number,

   b. set a well-formatted date, and

   c. have two blank lines between each entry.


   See that file for several examples.

2. Run `sbt release`. This command will first push the Docker image to Docker
   and then pushes the package to LaunchPad PPA. It takes forever for Launchpad
   to build. You receive errors over email.


TODO: If the Docker image is updated, you need to write a post-install script that


## Creating a new course VM for students

The course VM is configured to install software from the Ubuntu PPA. If you
need to build a new course VM, ensure that the PPA is up-to-date, as described
above.

[FILL]

## Updating the website

TODO: broken

`sbt compileWeb`

Since the website is in Arjun's personal folder, you can't publish changes.
But, you can build and preview changes within the VM.

1. Run `make website`

2. Visit `http://localhost:4000` using a Web browser on your host machine.

If you can publish the website, do `cd website; publish`

## Releasing open-source software

We publish the `support-code` and `submission` directories as open source
software. To do so, we use [Subtree merging].

First time setup:

~~~
git remote add -f submission git@github.com:cmpsci220/submission.git
git remote add -f support-code git@github.com:cmpsci220/support-code.git
~~~

To push code to the open source repositories:

~~~
$ git subtree push --prefix=submission submission master
$ git subtree push --prefix=support-code support-code master
~~~

To pull code:

~~~
git fetch --all
$ git subtree pull --prefix=submission submission master
$ git subtree pull --prefix=support-code support-code master
~~~
## Writing a new homework assignment

[FILL]


[Vagrant]: http://www.vagrantup.com
[Subtree merging]: http://blogs.atlassian.com/2013/05/alternatives-to-git-submodule-git-subtree/
