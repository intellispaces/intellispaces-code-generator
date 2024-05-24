package tech.intellispacesframework.annotationprocessor.artifact;

import tech.intellispacesframework.commons.type.TypeFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JavaArtifactContext {
  private String generatedClassCanonicalName;
  private final HashMap<String, Set<String>> imports = new HashMap<>();
  private final Set<String> staticImports = new HashSet<>();
  private final List<String> javaDocLines = new ArrayList<>();

  public void generatedClassCanonicalName(String canonicalName) {
    this.generatedClassCanonicalName = canonicalName;
  }

  public void addImport(Class<?> aClass) {
    addImport(aClass.getName());
  }

  public void addImports(Collection<String> canonicalNames) {
    canonicalNames.forEach(this::addImport);
  }

  public void addImport(String canonicalName) {
    String simpleName = TypeFunctions.getSimpleName(canonicalName);
    imports.computeIfAbsent(simpleName, k -> new LinkedHashSet<>()).add(canonicalName);
  }

  public Consumer<String> getImportConsumer() {
    return this::addImport;
  }

  public void addStaticImport(String canonicalName) {
    staticImports.add(canonicalName);
  }

  public void addJavaDocLine(String line) {
    javaDocLines.add(line);
  }

  public String generatedClassCanonicalName() {
    return generatedClassCanonicalName;
  }

  public String generatedClassSimpleName() {
    return TypeFunctions.getSimpleName(generatedClassCanonicalName);
  }

  public String packageName() {
    return TypeFunctions.getPackageName(generatedClassCanonicalName);
  }

  public String simpleName(String canonicalName) {
    String simpleName = TypeFunctions.getSimpleName(canonicalName);
    Set<String> set = imports.get(simpleName);
    if (canonicalName.equals(set.iterator().next())) {
      return simpleName;
    }
    return canonicalName;
  }

  public List<String> getImports() {
    return imports.values().stream()
        .flatMap(Set::stream)
        .filter(className -> !className.startsWith("java.lang."))
        .filter(className -> !className.equals(generatedClassCanonicalName))
        .sorted()
        .toList();
  }

  public List<String> getStaticImports() {
    return staticImports.stream().sorted().toList();
  }
}