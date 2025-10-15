#!/bin/bash

# Define the path to your .env file (adjust as needed)
ENV_FILE=".run/dev.env"

# Read each line from the environment file and export it.
while IFS= read -r line; do
    # Skip empty lines or comments starting with '#'
    if [[ "$line" =~ ^[[:space:]]*#[[:space:]]*$ ]] || [[ -z "${line// }" ]]; then
        continue
    fi

    # Split the line into key and value using '=' as a delimiter.
    IFS='=' read -r key_part value_part <<< "$line"

    # Trim leading/trailing whitespace from both parts.
    key=$(echo $key_part | xargs)
    value=$(echo $value_part | xargs)

    if [[ ! -z "$key" && "$value" != "" ]]; then
        export "$key=$value"
    fi
done < "$ENV_FILE"

# Optional: Print exported variables to verify
env | grep -E 'POSTGRES_|S3_'