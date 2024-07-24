FROM maven:3.9.6-eclipse-temurin-21-alpine AS maven

FROM python:3.12-alpine3.20 AS builder

ENV POETRY_NO_INTERACTION=1 \
  POETRY_VIRTUALENVS_IN_PROJECT=1 \
  POETRY_VIRTUALENVS_CREATE=1 \
  POETRY_CACHE_DIR=/tmp/poetry_cache \
  PYTHONFAULTHANDLER=1 \
  PYTHONUNBUFFERED=1 \
  PYTHONHASHSEED=random \
  PIP_DISABLE_PIP_VERSION_CHECK=on \
  PIP_DEFAULT_TIMEOUT=100 \
  POETRY_VERSION=1.8.3

RUN apk add --update --no-cache gcc libc-dev musl-dev linux-headers python3-dev
RUN pip install "poetry==$POETRY_VERSION"
WORKDIR /app
COPY pyproject.toml poetry.lock ./
RUN --mount=type=cache,target=$POETRY_CACHE_DIR poetry install --no-root --no-ansi

FROM python:3.12-alpine3.20 AS runtime

ENV VIRTUAL_ENV=/app/.venv \
    PATH="/app/.venv/bin:$PATH"

# Installing prerequisites
RUN apk add --update --no-cache bash curl shellcheck gawk git make docker docker-cli-compose openrc \
    && rm -rf /var/cache/apk/* \
RUN rc-update add docker boot

# Install shdoc
RUN git clone --recursive https://github.com/reconquest/shdoc /tmp/shdoc
RUN make install -C /tmp/shdoc

# Install java and maven
COPY --from=maven /opt/java/openjdk /opt/java/openjdk
COPY --from=maven /usr/share/maven /usr/share/maven
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

COPY --from=maven /usr/bin/mvn /usr/bin/mvn
ENV PATH="/opt/java/openjdk/bin:$PATH"
ENV MAVEN_HOME="/usr/share/maven"
ENV JAVA_HOME="/opt/java/openjdk"

# Create user
RUN addgroup -g 1000 app \
    && adduser --home /app  -G app -u 1000 app -D
USER app
WORKDIR /app

# Copy the Python virtual environment
COPY --chown=app:app --from=builder ${VIRTUAL_ENV} ${VIRTUAL_ENV}

CMD ["/bin/bash"]
