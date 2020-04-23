#!/bin/bash
### just launch impress with the arguments passed
echo "start Impress with the parameters provided"

echo $1 $2 $3 $4

$1 $2 $3 $4

##soffice --impress --show /home/darrell/ImpressTests/ChainTests/ShowTestOne.odp

echo "Impress is done"

exit 0

