#!/bin/bash
if [ -n "$1" ]
 then
   gitrepo={\"git\":{\"uri\":\"https://github.com/$1\"}}
   echo "Gitrepo: $gitrepo"
   csi="cf cs p-config-server standard config-service -c '$gitrepo'"
   $csi
   if [ "$?" -ne "0" ]; then
     echo $csi
     exit $?
   fi
 else
   echo "Usage: deploy-pws <github config-repo e.g. https://github.com/jigsheth57/config-repo>"
   echo "example: ./deploy-pws.sh https://github.com/jigsheth57/config-repo"
   exit 1
 fi
