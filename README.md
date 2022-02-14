# A Telegram bot 🤖 skeleton project

[![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

## Content
This projet file is a skeleton for implementing Telegram messenger application bot. When complete, it will integrate several features:

- Implementation of commands in menu
- Basic text replies
- Image processing filters

## Project description and tasks to do
The project is not complete and is intented to be completed in a live coding session. The full project instructions, in French, is available [as a PDF](https://inf1.begincoding.net/telebot.pdf) file with clickable links. The password is accessible on request.

## Audience
This bot is targeted at students with an existing background in programming, mainly in Java. It was made to demonstrate some things taught during the [*Computer science and communication* bachelor's degree at the School of engineering in Sion, Switzerland](https://www.hevs.ch/isc).

<p align="center">
  <a href="https://hevs.ch/isc">
  <img src="https://github.com/pmudry/telegrambot_students/blob/master/resources/ISC_Logo_EN_CMJN_200.png?raw=true"/>    
  </a>
</p>

## Deployment
When complete, the bot can be built and run indepently thanks to Maven packaging, i.e. running
```
mvn compile package
```

should result in a runnable `JAR` file in the `target` directory. Voilà !

⚠️ A word of caution though : please do not integrate the authentication token directly in your source code. A better way of integrating the token from outside the code is to set an environment variable and get it with a call to `System.getEnv(YOUR_ENV_VARIABLE)` where required. You have been warned !

## Demonstration of a full, running bot
A more complete and running instance of the bot, based on this template, can be reached through https://telegram.me/HEI_ISCbot if you want to test it and if it's running.

Enjoy and have fun programming 🎈 !

_Pierre-André Mudry_, February 2022

## License
This work is licensed under a
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg
