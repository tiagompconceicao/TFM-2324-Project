# Overview

This repository stores both projects that integrates a Proof of Concept for my Master's thesis. Providing to the end user an LLM based tool with capabilities to generate and instantiate `Docker containers`. The main goal of this system is to simplify and speed up cybersecurity processes, such as the creation and management of honeypots.


# Organisation

This PoC is composed by 2 servers, a backend developed using Spring framework and a frontend developed using the React framework.

 The backend manages the communication between the frontend server and LLMs, storing these conversations in a Firebase server. All code regarding the component is present in the `jvm/backend.project/src/main/kotlin/pt/isel/tfm/tc/backend/project`directory. 
The frontend allows the user to interact with the LLMs providing an user-friendly interface. All code of this component is present in the `node/tfm-tc-frontend/src`directory. 


# Configuration

In order to execute both server be aware to configure the LLMs API keys in the `jvm/backend.project/src/main/resources/application.properties` file.
The API keys must replaced the dummy values marked as `Insert-here-%Something%-API-key`.
