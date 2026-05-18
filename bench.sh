#!/usr/bin/env bash
# bench.sh — compile, inspect memory layout, and run JMH benchmarks
#
# USAGE
#   ./bench.sh [OPTIONS] [FILTER]
#
# OPTIONS
#   --layout      print JOL memory layout before running benchmarks
#   --layout-only print JOL memory layout and exit (skip JMH)
#   --gc          add GC allocation-rate profiler (-prof gc) to JMH run
#
# FILTER
#   Regex passed to JMH to select benchmarks (default: .* = all).
#   Matched against fully-qualified benchmark method name.
#
# EXAMPLES
#   ./bench.sh                              all benchmarks
#   ./bench.sh SwissValorNumber             only SwissValorNumber benchmarks
#   ./bench.sh --layout SwissValorNumber    JOL layout, then SwissValorNumber benchmarks
#   ./bench.sh --layout-only               layout inspection only
#   ./bench.sh --gc SwissValorNumberConstr  allocation rate for construction benchmarks

set -euo pipefail

VALHALLA_HOME=/Users/dfa/Library/Java/JavaVirtualMachines/valhalla-ea-27-jep401ea3+1-1/Contents/Home
export JAVA_HOME="$VALHALLA_HOME"
JAVA="$VALHALLA_HOME/bin/java"

LAYOUT=false
BENCH=true
GC_PROF=false
FILTER=".*"

for arg in "$@"; do
    case $arg in
        --layout)      LAYOUT=true ;;
        --layout-only) LAYOUT=true; BENCH=false ;;
        --gc)          GC_PROF=true ;;
        --*)           echo "Unknown flag: $arg" >&2; exit 1 ;;
        *)             FILTER="$arg" ;;
    esac
done

echo "==> compile"
./mvnw test-compile -q

if $LAYOUT; then
    echo "==> JOL layout"
    # -q suppresses Maven [INFO] lines; test stdout (layout tables) still prints
    ./mvnw test -q -DfailIfNoTests=false \
        -Dtest="SwissValorNumberLayoutTest,CoordinateLayoutTest"
fi

if $BENCH; then
    # Build test classpath once via Maven, then invoke JMH directly so we have
    # full control over JMH flags (avoids Maven exec-plugin's empty-arg problem).
    CP_FILE=$(mktemp /tmp/bench-cp.XXXXXX)
    ./mvnw -q dependency:build-classpath -DincludeScope=test \
        -Dmdep.outputFile="$CP_FILE"
    CP="target/test-classes:target/classes:$(cat "$CP_FILE")"
    rm -f "$CP_FILE"

    PROF_ARGS=()
    if $GC_PROF; then
        PROF_ARGS=(-prof gc)
        echo "==> JMH (filter: $FILTER, +gc profiler)"
    else
        echo "==> JMH (filter: $FILTER)"
    fi

    "$JAVA" --enable-preview -Djol.magicFieldOffset=true \
        -cp "$CP" \
        org.openjdk.jmh.Main "$FILTER" \
        "${PROF_ARGS[@]}" \
        -rf json -rff target/benchmark-results.json
fi
