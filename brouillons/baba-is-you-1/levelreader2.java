  /**
   * Read the tiles declaration
   * from the {@link LevelReader#TILES_FILES} file
   */
  static @NotNull Map<Character, Integer> readTiles(
      @NotNull Path levelDirectory
  ) throws IOException {
    Path elementsFile = getFile(levelDirectory, TILES_FILES);
    List<String> tilesContent = Files.readAllLines(elementsFile);

    Map<Character, Integer> result = new HashMap<>();
    for (String tileLine : tilesContent) {
      Matcher m = TILES_REGEX.matcher(tileLine);
      if (!m.find()) {
        throw new IllegalArgumentException(
            "Bad tile declaration [" + tileLine + "]");

      }
      Character character = m.group(1).charAt(0);
      String tile = m.group(2);
      int index = Arrays.binarySearch(Tiles.ALL_STRINGS, tile);
      if (index < 0) {
        throw new IllegalArgumentException(
            "Unknown tile [" + tile + "]");
      }
      result.put(character, index);
    }
    return result;
  }

  /**
   * Read the file content
   * from the {@link LevelReader#CONTENT_FILES} file
   * relying the declared tiles
   */
  static @NotNull LevelReaderResult readContent(
      @NotNull Path levelDirectory,
      @NotNull Map<Character, Integer> tiles
  ) throws IOException {
    Path elementsFile = getFile(levelDirectory, CONTENT_FILES);
    List<String> contentContent = Files.readAllLines(elementsFile);

    int height = contentContent.size();
    int width = contentContent.get(0).length();
    int[] content = new int[height * width];

    for (int lineIndex = 0; lineIndex < height; lineIndex++) {
      String contentLine = contentContent.get(lineIndex);
      if (contentLine.length() != width) {
        throw new IllegalArgumentException(
            "[" +
                contentLine +
                "] is not " +
                width +
                " characters long at line " +
                lineIndex +
                " of " +
                elementsFile.toAbsolutePath());
      }

      for (int columnIndex = 0; columnIndex < width; columnIndex++) {
        char c = contentLine.charAt(columnIndex);

        if (!tiles.containsKey(c)) {
          throw new IllegalArgumentException(
              "Unknown tile [" +
                  c +
                  "] at line " +
                  lineIndex +
                  " of " +
                  elementsFile.toAbsolutePath());
        }
        int position = (lineIndex * width) + columnIndex;
        content[position] = tiles.get(c);
      }
    }
    return new LevelReaderResult(width, height, content);
  }

  private static @NotNull Path getFile(
      @NotNull Path directory,
      @NotNull String fileName
  ) throws FileNotFoundException {
    Path file = directory.resolve(fileName);
    if (!Files.exists(file)) {
      throw new FileNotFoundException(
          file.toAbsolutePath().toString());
    }
    return file;
  }