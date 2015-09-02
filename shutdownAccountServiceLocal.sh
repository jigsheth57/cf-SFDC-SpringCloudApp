#!/bin/bash

kill -9 `ps -ef | grep accountService | grep -v grep | awk '{print $2}'`
