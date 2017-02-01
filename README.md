
# orders-aws

A microservices-demo service that provides ordering capabilities.

The code is in Scala and uses Spring, Jackson and the Java DynamoDB client.

For simplicity, error handling is achieved using runtime exceptions.

# Build

`GROUP=weaveworksdemos COMMIT=latest ./scripts/build.sh`

# Test

`./test/test.sh < python testing file >`. For example: `./test/test.sh unit.py`

# Manual test

``
