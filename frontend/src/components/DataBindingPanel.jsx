import { useEffect, useRef, useState } from "react";

import { executeWithProduct, getMockProducts } from "../lib/api";
import ConsolePanel from "./ConsolePanel";

const PYTHON_EXAMPLE = `pricing.finalPrice = (
    pricing.basePrice * (1 - pricing.discount)
)
print("Final price:", pricing.finalPrice)
`;

const JAVASCRIPT_EXAMPLE = `pricing.finalPrice =
    pricing.basePrice * (1 - pricing.discount);

console.log("Final price:", pricing.finalPrice);
`;

export default function DataBindingPanel({
  language,
  code,
  disabled = false,
  onBusyChange,
  onUseExample,
}) {
  const inFlight = useRef(false);

  const [products, setProducts] = useState([]);
  const [productId, setProductId] = useState("");
  const [running, setRunning] = useState(false);
  const [result, setResult] = useState(null);
  const [failure, setFailure] = useState(null);
  const [catalogError, setCatalogError] = useState("");

  useEffect(() => {
    let active = true;

    getMockProducts()
      .then((response) => {
        if (active) {
          setProducts(response);
          setProductId(response[0]?.id ?? "");
        }
      })
      .catch((error) => {
        if (active) {
          setCatalogError(error.message || "Cannot load mock products.");
        }
      });

    return () => {
      active = false;
    };
  }, []);

  const supported = ["python", "javascript"].includes(language);

  async function runWithData() {
    if (inFlight.current || disabled || !supported || !productId) {
      return;
    }

    inFlight.current = true;
    setRunning(true);
    onBusyChange(true);
    setResult(null);
    setFailure(null);

    try {
      setResult(
        await executeWithProduct({
          productId,
          language,
          code,
        }),
      );
    } catch (error) {
      setFailure(error);
    } finally {
      inFlight.current = false;
      setRunning(false);
      onBusyChange(false);
    }
  }

  function useExample() {
    const example =
      language === "javascript" ? JAVASCRIPT_EXAMPLE : PYTHON_EXAMPLE;

    onUseExample(example);
  }

  const execution = result?.execution ?? failure?.execution;
  const locked = disabled || running;

  return (
    <section className="panel data-panel" aria-label="Mock data binding">
      <h2>Mock Product Data Binding</h2>

      <p className="muted">
        Java queries an in-memory catalog and exposes pricing fields to the
        current Python or JavaScript script. This is not a MongoDB connection.
      </p>

      <p className="muted">
        Read: <code>pricing.basePrice</code>, <code>pricing.discount</code>.
        Write: <code>pricing.finalPrice</code>.
      </p>

      {catalogError && (
        <p className="console-error" role="alert">{catalogError}</p>
      )}

      <div className="library-controls">
        <label>
          Mock product
          <select
            value={productId}
            disabled={locked}
            onChange={(event) => setProductId(event.target.value)}
          >
            <option value="">Select a product</option>

            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.name} — base {product.basePrice.toFixed(2)}
              </option>
            ))}
          </select>
        </label>

        <button
          type="button"
          className="secondary-button"
          disabled={locked || !supported}
          onClick={useExample}
        >
          Insert binding example
        </button>

        <button
          type="button"
          className="primary-button"
          disabled={
            locked ||
            !supported ||
            !productId ||
            !code.trim() ||
            code.length > 20_000
          }
          onClick={runWithData}
        >
          {running ? "Processing…" : "Run current script with product"}
        </button>
      </div>

      {result && (
        <p role="status">
          {result.product.name}: final price {result.finalPrice.toFixed(2)}
        </p>
      )}

      {(running || result || failure) && (
        <ConsolePanel
          running={running}
          stdout={execution?.stdout ?? ""}
          stderr={execution?.stderr ?? ""}
          durationMs={execution?.durationMs ?? null}
          outputTruncated={execution?.outputTruncated ?? false}
          error={failure?.message ?? ""}
          errorCode={failure?.code ?? ""}
          guestStack={failure?.guestStack ?? []}
        />
      )}

      <p className="warning">
        Use this panel’s Run button for binding scripts. The main Run button
        does not inject product data.
      </p>
    </section>
  );
}