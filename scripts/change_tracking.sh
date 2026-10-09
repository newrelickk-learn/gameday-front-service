#!/bin/bash
NEW_RELIC_USER_KEY=${1:-KEY}
REGION=$(echo "${2:-${NEW_RELIC_REGION:-US}}" | tr '[:lower:]' '[:upper:]')
APP_NAME=${3:-${APP_NAME:-front-service}}
case "${REGION}" in
  US) ENDPOINT=https://api.newrelic.com/graphql ;;
  JP) ENDPOINT=https://api.jp.newrelic.com/graphql ;;
  *) echo "Unknown region: ${REGION}"; exit 1 ;;
esac
sed -i.bak "s/APP_NAME/${APP_NAME}/" scripts/change_tracking.query && rm -f scripts/change_tracking.query.bak
for i in `seq 1 100`; do
  echo "try #$i";
  RES=$(curl -X POST ${ENDPOINT} -H 'Content-Type: application/json' -H 'API-Key: '${NEW_RELIC_USER_KEY} --data @scripts/running1stPod.query)
  echo $RES
  if [ -z `echo $RES | grep \"count\":1` ]; then
    echo "No pod running yet";
  else
    echo "Some Pod Running";
    break;
  fi;
  sleep 30;
done

curl -X POST ${ENDPOINT} -H 'Content-Type: application/json' -H 'API-Key: '${NEW_RELIC_USER_KEY} --data @scripts/change_tracking.query