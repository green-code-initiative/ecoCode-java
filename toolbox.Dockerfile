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

RUN pip install "poetry==$POETRY_VERSION"
RUN apk add --update --no-cache gcc libc-dev musl-dev linux-headers python3-dev
WORKDIR /app
COPY pyproject.toml poetry.lock ./
RUN --mount=type=cache,target=$POETRY_CACHE_DIR poetry install --no-root --no-ansi

FROM python:3.12-alpine3.20 AS runtime

ENV VIRTUAL_ENV=/app/.venv \
    PATH="/app/.venv/bin:$PATH"

# Installing prerequisites
RUN apk add --update --no-cache bash curl shellcheck gawk git make \
    && rm -rf /var/cache/apk/*

# Install shdoc
RUN git clone --recursive https://github.com/reconquest/shdoc /tmp/shdoc
RUN make install -C /tmp/shdoc

# Create user
RUN addgroup -g 1000 app \
    && adduser --home /app  -G app -u 1000 app -D
USER app
WORKDIR /app

# Copy the Python virtual environment
COPY --chown=app:app --from=builder ${VIRTUAL_ENV} ${VIRTUAL_ENV}

CMD ["/bin/bash"]
