# RSA-encrypted-messenger

Initially, the aim of this project was to implement the RSA encryption scheme. Completing that, I then wrote a TCP instant messaging program that made use of my RSA implementation to allow for users to send encrypted messages to each other.

## Features

# Instant messaging
Users can send plaintext messages to other users connected to the running central server (`server.java`)
# Encrypted messaging
Users have the option of encrypting their messages (RSA encryption scheme). Ciphertext is generated clientside
and delivered to the recipient by the central server.
# Digital Signatures
If a user decides to encrypt their message, digital signatures will automatically used to enforce the integrity of the message during transit across the network. It also provides a means of authentication for the recipient; the recipient can authenticate the sender, if they can decrypt the digital signature with the senders public key.

### Quickstart guide
1. Start `server.java` on a host machine
2. Configure `client.java` to connect to the server running on the host machine by altering the values on
**Strong** __lines 6 and 7__ in `RSA.java`. By default `client.java` is set to connect to a server instance running `localhost`
port 25565
3. Run instances of `client.java` for each user.
4. Initially, you will need to setup values for public key pair (n,e). You will be prompted for values for n , p and e. Details for the conditions of these values are provided in the program and examples are provided below:
*  p = 97 , q = 101 , e = 251
*  p = 11 , q = 13 , e = 23
*  p and q must be large prime numbers. e must be relatively prime to p , q and (p-1)\*(q-1); 1 < e < (p-1)\*(q-1)

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
