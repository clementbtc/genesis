package com.projetloki.genesis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of Genesis' main function.
 *
 * @author Clément Roux
 */
final class GenesisMain {
  static void doMain(String[] args) throws ClassNotFoundException, IOException {
    if (args.length == 0) {
      printUsage();
      return;
    }
    Map<Param, String> paramToArg = Maps.newEnumMap(Param.class);
    Iterator<String> it = Arrays.asList(args).iterator();
    while (it.hasNext()) {
      String next = it.next();
      if (!next.startsWith("--")) {
        printUsage();
      }
      Param param = Param.NAME_TO_PARAM.get(next.substring(2));
      if (param == null || paramToArg.containsKey(param) || !it.hasNext()) {
        printUsage();
      }
      String value = it.next();
      paramToArg.put(param, value);
    }
    // Check that all required params are present
    for (Param param : Param.values()) {
      if (param.required && !paramToArg.containsKey(param)) {
        printUsage();
      }
    }
    Genesis.Builder builder = Genesis.builder();
    if (paramToArg.containsKey(Param.IMAGE_FOLDER_URI)) {
      String imageFolderUri = paramToArg.get(Param.IMAGE_FOLDER_URI);
      builder.setImageFolderUri(imageFolderUri);
    }
    File imageFolder = paramToArg.containsKey(Param.IMAGE_FOLDER) ?
        new File(paramToArg.get(Param.IMAGE_FOLDER)) : null;
    File out = paramToArg.containsKey(Param.OUT) ?
        new File(paramToArg.get(Param.OUT)) : null;
    CssModule module = extractModuleFromStaticField(
        paramToArg.get(Param.ENTRY_CLASS));
    builder.install(module);
    Genesis genesis = builder.build();
    if (imageFolder != null) {
      genesis.writeImages(imageFolder);
    }
    if (out != null) {
      genesis.writeCssFile(out);
      System.out.println("css written to " + out.getAbsolutePath());
    } else {
      System.out.println(genesis.getCss());
    }
  }

  private static void fail(String format, Object... args) {
    System.out.printf(format, args);
    System.out.println();
    System.exit(0);
  }

  private static void printUsage() {
    PrintStream out = System.out;
    out.print("usage: ");
    out.println(Genesis.class.getSimpleName());
    for (Param param : Param.values()) {
      out.print("  --");
      out.println(param.name);
      for (List<String> words : param.lines) {
        out.print("      ");
        int i = 0;
        for (String word : words) {
          if (56 <= i + word.length()) {
            // New line
            out.print("\n      ");
            i = 0;
          }
          i += word.length() + 1;
          System.out.print(word);
          System.out.print(' ');
        }
        System.out.println();
      }
    }
    // An example
    System.out.print("\nexample: ");
    System.out.print(Genesis.class.getSimpleName());
    for (Param param : Param.values()) {
      System.out.print(" \\\n  --");
      System.out.print(param.name);
      for (int i = param.name.length(); i < Param.MAX_NAME_LENGTH + 2; i++) {
        System.out.print(' ');
      }
      System.out.print(param.example);
    }
    System.exit(0);
  }

  static CssModule extractModuleFromStaticField(String className)
      throws ClassNotFoundException {
    List<CssModule> modules = Lists.newArrayList();
    Class<?> clazz = Class.forName(className);
    for (Field field : clazz.getDeclaredFields()) {
      int mod = field.getModifiers();
      if (Modifier.isPrivate(mod) || !Modifier.isStatic(mod)) {
        continue;
      }
      if (!CssModule.class.isAssignableFrom(field.getType())) {
        continue;
      }
      field.setAccessible(true);
      CssModule module;
      try {
        module = (CssModule) field.get(null);
        modules.add(module);
      } catch (IllegalAccessException ex) {
        // Should not happen
        throw new RuntimeException(ex);
      }
      if (module == null) {
        fail("static field %s in <%s> has null value",
            field.getName(), clazz.getName());
      }
    }
    if (modules.isEmpty()) {
      fail("no public or package-private static field assignable to %s",
          CssModule.class.getSimpleName());
    }
    if (2 <= modules.size()) {
      fail(
          "more than one public or package-private static fields " +
          "assignable to %s", CssModule.class.getSimpleName());
    }
    return modules.get(0);
  }

  private enum Param {
    ENTRY_CLASS("entry_class", true,
        "Class that declares a single public or package-private static field " +
        "assignable to " + CssModule.class.getSimpleName() + ".\n" +
        "Required.",
        "my.java.package.Foobar"),
    IMAGE_FOLDER("image_folder", false,
        "Path to a local directory in which background images are generated. " +
        "If not set, background images are not generated.",
        "/home/john/mysite/img"),
    IMAGE_FOLDER_URI("image_folder_url", false,
        "URI of the image directory. Absolute or relative to the URI of the " +
        "CSS or the document containing the CSS code. " +
        "<.> by default.",
        "img"),
    OUT("out", false,
        "Path to the output CSS file. Stdout by default.",
        "/home/john/mysite/style.css");
    static final ImmutableMap<String, Param> NAME_TO_PARAM;
    static final int MAX_NAME_LENGTH;
    static {
      int maxNameLength = 0;
      ImmutableMap.Builder<String, Param> builder = ImmutableMap.builder();
      for (Param param : Param.values()) {
        builder.put(param.name, param);
        maxNameLength = Math.max(param.name.length(), maxNameLength);
      }
      NAME_TO_PARAM = builder.build();
      MAX_NAME_LENGTH = maxNameLength;
    }

    final String name;
    final boolean required;
    final List<List<String>> lines;
    final String example;

    Param(String name, boolean required, String description, String example) {
      this.name = name;
      this.required = required;
      lines = Lists.newArrayList();
      for (String line : Splitter.on('\n').split(description)) {
        List<String> words = ImmutableList.copyOf(Splitter.on(' ').split(line));
        lines.add(words);
      }
      this.example = example;
    }
  }
}
