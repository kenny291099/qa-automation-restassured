#!/bin/bash

# Restful Booker API Test Execution Script
# This script provides convenient commands to run different test suites

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script configuration
SCRIPT_NAME=$(basename "$0")
MAVEN_OPTS="-Xmx2048m"

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to print script usage
print_usage() {
    echo "Usage: $SCRIPT_NAME [OPTION]"
    echo ""
    echo "API Test Execution Script for Restful Booker"
    echo ""
    echo "Options:"
    echo "  smoke           Run smoke tests only"
    echo "  regression      Run regression tests only"
    echo "  all             Run all tests"
    echo "  clean           Clean previous test results"
    echo "  compile         Compile the project"
    echo "  report          Generate Allure report"
    echo "  serve           Generate and serve Allure report"
    echo "  health          Check API health"
    echo "  help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $SCRIPT_NAME smoke"
    echo "  $SCRIPT_NAME regression"
    echo "  $SCRIPT_NAME clean compile smoke"
    echo ""
}

# Function to check prerequisites
check_prerequisites() {
    print_message $BLUE "Checking prerequisites..."
    
    # Check Java version
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_message $GREEN "✓ Java $JAVA_VERSION found"
        else
            print_message $RED "✗ Java 17 or higher required (found Java $JAVA_VERSION)"
            exit 1
        fi
    else
        print_message $RED "✗ Java not found"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        print_message $GREEN "✓ Maven $MVN_VERSION found"
    else
        print_message $RED "✗ Maven not found"
        exit 1
    fi
    
    print_message $GREEN "All prerequisites satisfied!"
    echo ""
}

# Function to check API health
check_api_health() {
    print_message $BLUE "Checking API health..."
    
    if curl -f -s https://restful-booker.herokuapp.com/ping > /dev/null; then
        print_message $GREEN "✓ API is responding"
    else
        print_message $YELLOW "⚠ API health check failed - tests may fail"
    fi
    echo ""
}

# Function to clean previous results
clean_results() {
    print_message $BLUE "Cleaning previous test results..."
    mvn clean
    print_message $GREEN "✓ Clean completed"
    echo ""
}

# Function to compile project
compile_project() {
    print_message $BLUE "Compiling project..."
    mvn compile test-compile
    print_message $GREEN "✓ Compilation completed"
    echo ""
}

# Function to run smoke tests
run_smoke_tests() {
    print_message $BLUE "Running smoke tests..."
    mvn test -Dgroups=smoke
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        print_message $GREEN "✓ Smoke tests completed successfully"
    else
        print_message $RED "✗ Smoke tests failed"
    fi
    echo ""
    return $exit_code
}

# Function to run regression tests
run_regression_tests() {
    print_message $BLUE "Running regression tests..."
    mvn test -Dgroups=regression
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        print_message $GREEN "✓ Regression tests completed successfully"
    else
        print_message $RED "✗ Regression tests failed"
    fi
    echo ""
    return $exit_code
}

# Function to run all tests
run_all_tests() {
    print_message $BLUE "Running all tests..."
    mvn test
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        print_message $GREEN "✓ All tests completed successfully"
    else
        print_message $RED "✗ Some tests failed"
    fi
    echo ""
    return $exit_code
}

# Function to generate Allure report
generate_report() {
    print_message $BLUE "Generating Allure report..."
    mvn allure:report
    print_message $GREEN "✓ Report generated in target/site/allure-maven-plugin/"
    echo ""
}

# Function to serve Allure report
serve_report() {
    print_message $BLUE "Generating and serving Allure report..."
    print_message $YELLOW "Note: This will open the report in your default browser"
    mvn allure:serve
}

# Function to display test summary
display_summary() {
    print_message $BLUE "Test Execution Summary"
    echo "======================"
    
    if [ -f "target/surefire-reports/testng-results.xml" ]; then
        # Extract test results from TestNG results
        TOTAL=$(grep -o 'total="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        PASSED=$(grep -o 'passed="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        FAILED=$(grep -o 'failed="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        SKIPPED=$(grep -o 'skipped="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        
        print_message $GREEN "Total: $TOTAL"
        print_message $GREEN "Passed: $PASSED"
        print_message $RED "Failed: $FAILED"
        print_message $YELLOW "Skipped: $SKIPPED"
    else
        print_message $YELLOW "No test results found"
    fi
    
    echo ""
    print_message $BLUE "Reports available at:"
    echo "• TestNG: target/surefire-reports/index.html"
    echo "• Allure: target/site/allure-maven-plugin/index.html"
    echo ""
}

# Main script logic
main() {
    if [ $# -eq 0 ]; then
        print_usage
        exit 1
    fi
    
    # Set Maven options
    export MAVEN_OPTS="$MAVEN_OPTS"
    
    check_prerequisites
    
    # Process arguments
    for arg in "$@"; do
        case $arg in
            smoke)
                check_api_health
                run_smoke_tests
                ;;
            regression)
                check_api_health
                run_regression_tests
                ;;
            all)
                check_api_health
                run_all_tests
                ;;
            clean)
                clean_results
                ;;
            compile)
                compile_project
                ;;
            report)
                generate_report
                ;;
            serve)
                serve_report
                ;;
            health)
                check_api_health
                ;;
            help)
                print_usage
                exit 0
                ;;
            *)
                print_message $RED "Unknown option: $arg"
                print_usage
                exit 1
                ;;
        esac
    done
    
    # Display summary if tests were run
    if [[ "$*" =~ (smoke|regression|all) ]]; then
        display_summary
    fi
    
    print_message $GREEN "Script execution completed!"
}

# Execute main function with all arguments
main "$@"
