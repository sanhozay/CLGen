# Syntax Highlighting

## Vim

Support for syntax highlighting of CLGen source files is provided for the Vim
editor. It assumes that your source files are created with a `.clg` extension.

The release package for Vim syntax highlighting is available from <https://github.com/sanhozay/CLGen/releases>

If you are building CLGen from source and have the source distribution, the release package can be built using Gradle:

```
./gradlew vim
```

The Vim package is created in the `build/release` directory.

### Package Installation

#### 1. In your `~/.vim` directory, create a new directory called `pack/clgen`:

```
mkdir -p ~/.vim/pack/clgen
```

#### 2. Copy the CLGen-Vim-x.x.x.zip file into that new directory:

Where `x.x.x` corresponds to the version of CLGen that you are installing, e.g. 1.2.2.

```
cp CLGen-Vim-x.x.x.zip ~/.vim/pack/clgen
```

#### 3. Switch to the directory and unpack the archive:

```
cd ~/.vim/pack/clgen
unzip CLGen-Vim-x.x.x.zip
```

#### 4. Delete the zip archive

```
rm CLGen-Vim-x.x.x.zip
```

#### 4. Restart Vim.
