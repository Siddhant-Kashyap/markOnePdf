export interface FileMetaData {
  id: string;
  originalName: string;
  storagePath: string;
  contentType: string;
  size: number;
  createdAt: string;
  expiresAt: string;
}

export interface ConversionJob {
  id: string;
  jobStatus: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";
  conversionType: string;
  inputFilesIds: string[];
  outputFileId: string | null;
  createdAt: string;
  updatedAt: string;
  errorMessage: string | null;
}

export async function uploadFiles(files: File[]): Promise<FileMetaData[]> {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  const res = await fetch("/api/upload", {
    method: "POST",
    body: formData,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Upload failed");
  }

  return res.json();
}

export async function createJob(
  fileIds: string[],
  conversionType: string
): Promise<ConversionJob> {
  const res = await fetch("/api/jobs", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ fileIds, conversionType }),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Failed to create job");
  }

  return res.json();
}

export async function getJobStatus(jobId: string): Promise<ConversionJob> {
  const res = await fetch(`/api/jobs/${jobId}`);

  if (!res.ok) {
    throw new Error("Failed to fetch job status");
  }

  return res.json();
}

export function getDownloadUrl(jobId: string): string {
  return `/api/jobs/${jobId}/download`;
}
