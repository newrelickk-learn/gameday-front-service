ARG COMMIT_SHA="sha"
ARG RELEASE_TAG="dev"

FROM node:18-alpine
ARG COMMIT_SHA="sha"
ARG RELEASE_TAG="dev"
WORKDIR /js/front/
COPY ./src/main/js/react/front/src/ /js/front/src
COPY ./src/main/js/react/front/public/ /js/front/public
RUN ls -la /js/front/public
COPY ./src/main/js/react/front/.env /js/front/
COPY ./src/main/js/react/front/package.json /js/front/
COPY ./src/main/js/react/front/package-lock.json /js/front/
COPY ./src/main/js/react/front/tsconfig.json /js/front/
ENV REACT_APP_VERSION=$RELEASE_TAG
RUN echo $REACT_APP_VERSION
RUN npm install
RUN echo REACT_APP_VERSION; export REACT_APP_VERSION=$REACT_APP_VERSION; REACT_APP_VERSION=$REACT_APP_VERSION npx react-scripts build

FROM amazoncorretto:17-alpine
ARG COMMIT_SHA="sha"
ARG RELEASE_TAG="dev"

RUN apk update
RUN apk add --no-cache ca-certificates && update-ca-certificates
WORKDIR /build
COPY ./src/ /build/src
COPY ./gradle/ /build/gradle
COPY ./gradlew /build/gradlew
COPY ./build.gradle /build/build.gradle
COPY ./settings.gradle /build/settings.gradle
COPY ./run.sh /build/run.sh
COPY --from=0 /js/front/build/ /jsbuild/
RUN ls -la /jsbuild/;cp -r /jsbuild/static/* /build/src/main/resources/static/;cp -r /jsbuild/index.html /build/src/main/resources/templates/
RUN ./gradlew build && ./gradlew downloadNewrelic && ./gradlew unzipNewrelic

FROM amazoncorretto:17-alpine
ARG COMMIT_SHA="sha"
ARG RELEASE_TAG="dev"
RUN apk update
RUN apk add --no-cache ca-certificates && update-ca-certificates

COPY --from=1 /build/build/libs/frontservice-0.0.1-SNAPSHOT.jar /app/frontservice.jar
COPY --from=1 /build/newrelic/ /newrelic
COPY --from=1 /build/run.sh /run.sh
ENV NEW_RELIC_METADATA_COMMIT=$COMMIT_SHA
ENV NEW_RELIC_METADATA_RELEASE_TAG=$RELEASE_TAG

RUN echo "${NEW_RELIC_METADATA_RELEASE_TAG} ${NEW_RELIC_METADATA_COMMIT}"

ENTRYPOINT ["/run.sh"]