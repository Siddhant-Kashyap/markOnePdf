"use client";

import { useState, useEffect, useRef } from "react";
import {
  uploadFiles,
  createJob,
  getJobStatus,
  getDownloadUrl,
  type ConversionJob,
} from "@/lib/api";

type ConversionType =
  | "IMAGE_TO_PDF"
  | "MARKDOWN_TO_PDF"
  | "HTML_TO_PDF"
  | "MERGE_PDF";

const conversionOptions: {
  value: ConversionType;
  label: string;
  accept: string;
}[] = [
  {
    value: "IMAGE_TO_PDF",
    label: "Image to PDF",
    accept: "image/png,image/jpeg,image/webp",
  },
  { value: "MARKDOWN_TO_PDF", label: "Markdown to PDF", accept: ".md" },
  { value: "HTML_TO_PDF", label: "HTML to PDF", accept: ".html,.htm" },
  { value: "MERGE_PDF", label: "Merge PDFs", accept: ".pdf" },
];

type Status = "idle" | "uploading" | "processing" | "completed" | "failed";

export default function Home() {
  const [conversionType, setConversionType] =
    useState<ConversionType>("IMAGE_TO_PDF");
  const [files, setFiles] = useState<File[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [status, setStatus] = useState<Status>("idle");
  const [job, setJob] = useState<ConversionJob | null>(null);
  const [error, setError] = useState<string | null>(null);
  const pollingRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const selectedOption = conversionOptions.find(
    (o) => o.value === conversionType
  )!;

  useEffect(() => {
    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
  }, []);

  function reset() {
    setFiles([]);
    setStatus("idle");
    setJob(null);
    setError(null);
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
  }

  function handleDrop(e: React.DragEvent) {
    e.preventDefault();
    setIsDragging(false);
    const dropped = Array.from(e.dataTransfer.files);
    setFiles((prev) => [...prev, ...dropped]);
  }

  function handleFileInput(e: React.ChangeEvent<HTMLInputElement>) {
    if (e.target.files) {
      setFiles((prev) => [...prev, ...Array.from(e.target.files!)]);
    }
  }

  function removeFile(index: number) {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  }

  function pollJob(jobId: string) {
    pollingRef.current = setInterval(async () => {
      try {
        const updated = await getJobStatus(jobId);
        setJob(updated);

        if (updated.jobStatus === "COMPLETED") {
          setStatus("completed");
          if (pollingRef.current) clearInterval(pollingRef.current);
        } else if (updated.jobStatus === "FAILED") {
          setStatus("failed");
          setError(updated.errorMessage || "Conversion failed");
          if (pollingRef.current) clearInterval(pollingRef.current);
        }
      } catch {
        setStatus("failed");
        setError("Lost connection while checking status");
        if (pollingRef.current) clearInterval(pollingRef.current);
      }
    }, 2000);
  }

  async function handleConvert() {
    if (files.length === 0) return;

    setError(null);
    setStatus("uploading");

    try {
      const uploaded = await uploadFiles(files);
      const fileIds = uploaded.map((f) => f.id);

      setStatus("processing");
      const newJob = await createJob(fileIds, conversionType);
      setJob(newJob);

      pollJob(newJob.id);
    } catch (err) {
      setStatus("failed");
      setError(err instanceof Error ? err.message : "Something went wrong");
    }
  }

  const statusMessage: Record<Status, string> = {
    idle: "",
    uploading: "Uploading files...",
    processing: "Converting... this may take a moment",
    completed: "Conversion complete!",
    failed: "Something went wrong",
  };

  return (
    <div className="flex flex-col flex-1 items-center bg-zinc-50 font-sans dark:bg-zinc-950">
      <header className="w-full border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
        <div className="max-w-5xl mx-auto flex items-center justify-between px-6 py-4">
          <h1 className="text-xl font-bold tracking-tight text-zinc-900 dark:text-zinc-100">
            FastConvert
          </h1>
          <span className="text-sm text-zinc-500">
            Free Online PDF Converter
          </span>
        </div>
      </header>

      <main className="flex flex-1 w-full max-w-3xl flex-col items-center gap-8 px-6 py-12">
        <div className="text-center">
          <h2 className="text-3xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-100">
            Convert any file to PDF in seconds
          </h2>
          <p className="mt-2 text-zinc-500 dark:text-zinc-400">
            Drop your images, markdown, or HTML files — get a PDF back instantly. Free, no signup, no watermarks.
          </p>
        </div>

        {/* Conversion type picker */}
        <div className="flex flex-wrap justify-center gap-2">
          {conversionOptions.map((option) => (
            <button
              key={option.value}
              disabled={status !== "idle"}
              onClick={() => {
                setConversionType(option.value);
                setFiles([]);
              }}
              className={`rounded-full px-4 py-2 text-sm font-medium transition-colors disabled:opacity-50 ${
                conversionType === option.value
                  ? "bg-zinc-900 text-white dark:bg-zinc-100 dark:text-zinc-900"
                  : "bg-white text-zinc-700 border border-zinc-300 hover:bg-zinc-100 dark:bg-zinc-800 dark:text-zinc-300 dark:border-zinc-700 dark:hover:bg-zinc-700"
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>

        {/* Drop zone — only show when idle */}
        {status === "idle" && (
          <>
            <div
              onDragOver={(e) => {
                e.preventDefault();
                setIsDragging(true);
              }}
              onDragLeave={() => setIsDragging(false)}
              onDrop={handleDrop}
              className={`w-full rounded-xl border-2 border-dashed p-12 text-center transition-colors ${
                isDragging
                  ? "border-blue-500 bg-blue-50 dark:bg-blue-950"
                  : "border-zinc-300 bg-white dark:border-zinc-700 dark:bg-zinc-900"
              }`}
            >
              <p className="text-zinc-500 dark:text-zinc-400 mb-4">
                Drag & drop files here, or click to browse
              </p>
              <label className="inline-flex cursor-pointer items-center rounded-lg bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200">
                Choose Files
                <input
                  type="file"
                  multiple
                  accept={selectedOption.accept}
                  onChange={handleFileInput}
                  className="hidden"
                />
              </label>
            </div>

            {/* File list */}
            {files.length > 0 && (
              <div className="w-full space-y-4">
                <h3 className="text-sm font-medium text-zinc-700 dark:text-zinc-300">
                  {files.length} file{files.length > 1 ? "s" : ""} selected
                </h3>
                <ul className="space-y-2">
                  {files.map((file, i) => (
                    <li
                      key={i}
                      className="flex items-center justify-between rounded-lg border border-zinc-200 bg-white px-4 py-3 dark:border-zinc-700 dark:bg-zinc-900"
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <span className="text-sm text-zinc-900 dark:text-zinc-100 truncate">
                          {file.name}
                        </span>
                        <span className="text-xs text-zinc-400">
                          {(file.size / 1024).toFixed(1)} KB
                        </span>
                      </div>
                      <button
                        onClick={() => removeFile(i)}
                        className="text-zinc-400 hover:text-red-500 transition-colors text-sm"
                      >
                        Remove
                      </button>
                    </li>
                  ))}
                </ul>
                <button
                  onClick={handleConvert}
                  className="w-full rounded-lg bg-blue-600 px-5 py-3 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
                >
                  Convert to PDF
                </button>
              </div>
            )}
          </>
        )}

        {/* Status area */}
        {status !== "idle" && (
          <div className="w-full rounded-xl border border-zinc-200 bg-white p-8 dark:border-zinc-700 dark:bg-zinc-900">
            <div className="flex flex-col items-center gap-4">
              {/* Spinner for uploading/processing */}
              {(status === "uploading" || status === "processing") && (
                <div className="h-10 w-10 animate-spin rounded-full border-4 border-zinc-200 border-t-blue-600" />
              )}

              {/* Checkmark for completed */}
              {status === "completed" && (
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-100 dark:bg-green-900">
                  <svg
                    className="h-6 w-6 text-green-600 dark:text-green-400"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={2}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                </div>
              )}

              {/* X for failed */}
              {status === "failed" && (
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-red-100 dark:bg-red-900">
                  <svg
                    className="h-6 w-6 text-red-600 dark:text-red-400"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={2}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </div>
              )}

              <p className="text-sm font-medium text-zinc-700 dark:text-zinc-300">
                {statusMessage[status]}
              </p>

              {error && (
                <p className="text-sm text-red-500 text-center">{error}</p>
              )}

              {/* Download button */}
              {status === "completed" && job && (
                <a
                  href={getDownloadUrl(job.id)}
                  download
                  className="rounded-lg bg-blue-600 px-6 py-3 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
                >
                  Download PDF
                </a>
              )}

              {/* Convert another button */}
              {(status === "completed" || status === "failed") && (
                <button
                  onClick={reset}
                  className="text-sm text-zinc-500 hover:text-zinc-700 dark:hover:text-zinc-300 transition-colors"
                >
                  Convert another file
                </button>
              )}
            </div>
          </div>
        )}
      </main>

      <footer className="w-full border-t border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
        <div className="max-w-5xl mx-auto px-6 py-4 text-center text-sm text-zinc-400">
          FastConvert — Free Online PDF Converter
        </div>
      </footer>
    </div>
  );
}
