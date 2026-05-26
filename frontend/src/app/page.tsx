"use client";

import { useState } from "react";

type ConversionType = "image-to-pdf" | "markdown-to-pdf" | "html-to-pdf" | "merge-pdfs";

const conversionOptions: { value: ConversionType; label: string; accept: string }[] = [
  { value: "image-to-pdf", label: "Image to PDF", accept: "image/png,image/jpeg,image/webp" },
  { value: "markdown-to-pdf", label: "Markdown to PDF", accept: ".md" },
  { value: "html-to-pdf", label: "HTML to PDF", accept: ".html,.htm" },
  { value: "merge-pdfs", label: "Merge PDFs", accept: ".pdf" },
];

export default function Home() {
  const [conversionType, setConversionType] = useState<ConversionType>("image-to-pdf");
  const [files, setFiles] = useState<File[]>([]);
  const [isDragging, setIsDragging] = useState(false);

  const selectedOption = conversionOptions.find((o) => o.value === conversionType)!;

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

  return (
    <div className="flex flex-col flex-1 items-center bg-zinc-50 font-sans dark:bg-zinc-950">
      <header className="w-full border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
        <div className="max-w-5xl mx-auto flex items-center justify-between px-6 py-4">
          <h1 className="text-xl font-bold tracking-tight text-zinc-900 dark:text-zinc-100">
            Mark-1
          </h1>
          <span className="text-sm text-zinc-500">File Conversion Platform</span>
        </div>
      </header>

      <main className="flex flex-1 w-full max-w-3xl flex-col items-center gap-8 px-6 py-12">
        <div className="text-center">
          <h2 className="text-3xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-100">
            Convert your files to PDF
          </h2>
          <p className="mt-2 text-zinc-500 dark:text-zinc-400">
            Upload files, convert instantly, download. No signup required.
          </p>
        </div>

        <div className="flex flex-wrap justify-center gap-2">
          {conversionOptions.map((option) => (
            <button
              key={option.value}
              onClick={() => {
                setConversionType(option.value);
                setFiles([]);
              }}
              className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                conversionType === option.value
                  ? "bg-zinc-900 text-white dark:bg-zinc-100 dark:text-zinc-900"
                  : "bg-white text-zinc-700 border border-zinc-300 hover:bg-zinc-100 dark:bg-zinc-800 dark:text-zinc-300 dark:border-zinc-700 dark:hover:bg-zinc-700"
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>

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
            <button className="w-full rounded-lg bg-blue-600 px-5 py-3 text-sm font-medium text-white hover:bg-blue-700 transition-colors">
              Convert to PDF
            </button>
          </div>
        )}
      </main>

      <footer className="w-full border-t border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
        <div className="max-w-5xl mx-auto px-6 py-4 text-center text-sm text-zinc-400">
          Mark-1 File Conversion Platform
        </div>
      </footer>
    </div>
  );
}
