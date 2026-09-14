export const LANGUAGES = [
  { id: "python", label: "Python", executable: true, extension: "py" },
  { id: "javascript", label: "JavaScript", executable: true, extension: "js" },
  { id: "java", label: "Java", executable: false, extension: "java" },
];

export const INITIAL_SCRIPTS = {
  python: `price = 100
discount = 0.15

final_price = price * (1 - discount)
print("Final price:", final_price)
`,
  javascript: `const price = 85;

if (price <= 0) {
  throw new Error("Price must be positive");
}

console.log("Validation passed:", price);
`,
  java: `// Reference only: Java execution is not enabled .
// Trusted Java code runs in the Spring Boot backend.

class PricingRepository {
    public double findBasePrice() {
        return 100.0;
    }
}
`,
};