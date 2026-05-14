# Getting Started

## 🔧 Generate RSA Key Pair

use `openssl` to generate the keys from the command line:

### 1. Generate a 2048-bit Private Key
```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
```

### 2. Extract the Public Key from the Private Key
```bash
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

Now you have:
- `private_key.pem` — used to **sign** JWTs
- `public_key.pem` — used to **verify** JWTs
