#!/usr/bin/env python3

# String to count characters for
url_string = "https://engage.aws93-l13.aws93.lab.engage.int.ringcentral.com/api/v1/calls/recordings/?v=1&accountId=10930001&bucket=aws93-l13-r"

# Count the number of characters
character_count = len(url_string)

# Display the result
print(f"String: {url_string}")
print(f"Number of characters: {character_count}")

# Optional: Show some additional string statistics
print(f"\nAdditional statistics:")
print(f"Number of characters (excluding spaces): {len(url_string.replace(' ', ''))}")
print(f"Number of words: {len(url_string.split())}")
print(f"Number of '/' characters: {url_string.count('/')}")
print(f"Number of '.' characters: {url_string.count('.')}")