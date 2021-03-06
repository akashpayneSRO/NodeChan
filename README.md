# NodeChan
NodeChan is a completely decentralized peer-to-peer anonymous messageboard client. There are no central servers, and it can never be censored, turned off, shut down, or anything like that. Threads and posts are passed through the network by the clients themselves, not by a vulnerable central server.

This project is inspired by the peer-to-peer nature of Bittorrent, the decentralization of APRS on digital ham radio, and the anonymity of the *chan imageboards.

## User Manual
### Installation
The latest stable binary can be downloaded from [SquidTech](http://squid-tech.com/nodechan.html) and run.

If you would like to build NodeChan from source, clone this repository and type `make jar` to compile everything.

If you're looking for a one-liner for cloning and compiling:
`git clone https://github.com/joshiemoore/NodeChan && cd NodeChan && make jar`


### Running
If you are running the pre-compiled binary, all you have to do is type "java -jar NodeChan.jar \[-options\]" on your command line.

If you are running from source, run "./run.sh \[-options\]", or otherwise run the NodeChan.class file that was created as a result of compilation. Again, make sure to include the "lib/*" directory in your classpath.

* Include the command-line argument "-nogui" to run NodeChan in console mode
* Include the command-line argument "-local" to run NodeChan in LAN mode (see section "LAN Mode")
* Include the command-line argument "-nohello" to not send 'hello-packets' (not recommended, see format.txt)
* Include the command-line argument "-noinitpeer" to not connect to an initial peer from the tracker


### Operation
When NodeChan starts, the program will first attempt to enable UPnP port mapping. If your router does not support UPnP, you will need to manually forward port 13370 in order to receive content from the NodeChan network. Instructions on port forwarding for your router model can be readily found online.

Next, if you are not in LAN mode, you will be prompted to enter a peer tracker URL. It is recommended to leave this as default, unless you are hosting your own peer tracker. See section "Peer Tracker" for more information.

Finally, if you are not in LAN mode, NodeChan will attempt to retrieve an initial peer from the peer tracker. After that, you will be sent to the main interface.


### Graphical Mode
![NodeChan example](http://squid-tech.com/NodeChan_example.png)

When you reach the main screen, you will gradually see thread titles start to list themselves as you receive threads from your peers. Double click on a displayed thread to read that thread or reply to it. Your current number of peers is listed at the bottom-right.

If you find that threads are loading and moving around too quickly, you can disable autorefresh by unchecking the "Threads->Autorefresh" main menu option. Then you will need to manually refresh the thread list by clicking "Threads->Refresh".

Click on the "New Thread" button at the bottom-left side of the screen to post your own thread. Thread titles are currently limited to 50 characters, and post texts are currently limited to 256 characters. This limit is planned to be increased in future releases. Note that you may type your posts outside of these limits, but your peers will receive your posts trimmed to the limits.

### Blocking
You can block a user by right-clicking on one of the threads they have posted or one of the posts they have made and clicking "Block".

When you block a user, all posts and threads they have created will be hidden. You will not receive any additional posts or threads from the blocked user. The user will be removed from your peer list if they are on it, and you will not be able to re-add the blocked user as a peer. Blocked users will remain blocked until you restart NodeChan.


### Console Mode
Console mode is currently unsupported, and is likely to be removed in future versions. Presently, console mode is only used for debugging purposes. Use the "-nogui" command line argument to access console mode, and enter "help" for a list of commands.


### Peers
Peers are the most important aspect of NodeChan. They are the other network users that you have connected to. When you create a thread or post, your content will be sent to your peers, who will send it to all of their peers, and so on. In this way, posts are able to be propagated across the entire network without the use of a central server.

The more peers a client has, the further and more thoroughly their posts will be able to propagate. However, having an excessive number of peers could lead to reduced local performance, as well as cluttered traffic on the NodeChan network. It is recommended to choose a reasonable number of peers that balances these considerations.

Peers will time out and be removed from your peer list if they do not send you any data for a length of time. This prevents you from wasting resources sending content to peers that may have disconnected from the network.

To add a peer with a known IP address, use the "Peers->Add Peer..." option from the main menu. Use the "Peers->Get Peer From Tracker" option to retrieve a peer from the peer tracker. When you add a peer, they also automatically add you as a peer, unless you have specified the "-nohello" argument.


### Peer Tracker
The peer tracker is a PHP script that I am hosting on my website. When the script is loaded (and your IP is provided in the "?ip=" query string), the script returns the IP address of a random node that is also connected to the NodeChan network. Your IP is also added to the database, so other users can add you as a peer. When you click the "Peers->"Get Peer From Tracker" main menu option, the client automatically does all of this and then adds the retrieved peer to your peer list.

IP addresses in the peer tracker time out if they are not heard from for 10 minutes and are removed from the database.

The peer tracker is the only part of the NodeChan system that could be considered somewhat "centralized", but it is also completely optional. Users could share their IP addresses and connect directly to one another, forming their own small networks without ever interacting with a traditional web server.

Users could create their own peer trackers by hosting their own PHP scripts that behave similarly to the described behavior. Then simply provide the URL of your tracking script (including the query string "?ip=") when prompted by NodeChan. Maybe your tracker could have a whitelist and only let your friends in, or some other interesting properties.


### LAN Mode
If the argument "-local" is included when NodeChan is run, you will be able to connect to peers on your local network (192.168.1.*). UPnP port mapping will not be enabled, and you will not be able to retrieve any peers from the peer tracker. You will not be able to connect to any peers outside of your local network.


## Contributing
While NodeChan is already almost completely functional, it is not finished, nor is it particularly pretty or user-friendly. We are seeking contributors to help improve existing code and implement new features.

A few planned features include:
* Improvements to the graphical interface
* Security features, such as end-to-end encryption, etc.
* More social features, such as direct messaging thread participants
* Longer messages than 256 characters
* Support for attaching images to posts

If you would like to contribute, whether in terms of feature development, code refactoring, documentation, or otherwise, feel free to send a pull request! If there is a feature that you would like to see implemented, or a bug you would like to see fixed, create an issue! I will review all of these as quickly as possible.


## Problems
NodeChan is not perfect, and there are some issues that we will need to address as the project grows.

### Security
There are a lot of security concerns. Messages are currently not encrypted, and peers are added based on IP address. As a result, it is recommended to route your connection to NodeChan through a proxy or VPN.

### Propagation
The current post-propagation scheme could lead to situations where users become isolated from the network, and posts may not reach them. It will be important to implement more techniques ensuring that posts are propagated evenly and reliably across the network.

### Abuse
There are no moderators on NodeChan. By its very nature, it would be impossible to centrally moderate. Therefore, procedures should be developed that enable users to self-moderate and hide content that they find overly abusive or annoying. IP block lists, personal word filters, and other techniques could be used to allow users to hide content they don't like.
