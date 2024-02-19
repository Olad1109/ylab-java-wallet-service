FROM bellsoft/liberica-openjdk-alpine:17.0.9-11-x86_64
WORKDIR /app
COPY . /app
RUN javac src/main/java/com/example/Wallet.java
CMD ["java", "WalletService"]