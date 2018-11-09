#!/bin/bash

release_name=$(helm ls | awk 'FNR > 1 {if ($5='sfdcapps-1.0.0') print $1}')
helm delete --purge $release_name
