import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "FastConvert — Free Online File to PDF Converter",
  description:
    "Convert images, markdown, and HTML to PDF online for free. Merge multiple PDFs instantly. No signup, no watermarks — just drag, drop, and download.",
  keywords: [
    "convert image to pdf",
    "merge pdf online",
    "html to pdf converter",
    "markdown to pdf",
    "free pdf converter",
    "online file converter",
    "png to pdf",
    "jpg to pdf",
  ],
  openGraph: {
    title: "FastConvert — Free Online File to PDF Converter",
    description:
      "Drag, drop, convert. Turn images, HTML, and markdown into PDFs instantly. Merge PDFs in seconds. No signup required.",
    type: "website",
    siteName: "FastConvert",
  },
  twitter: {
    card: "summary_large_image",
    title: "FastConvert — Free Online File to PDF Converter",
    description:
      "Convert images, markdown, and HTML to PDF for free. No signup, no watermarks.",
  },
  robots: {
    index: true,
    follow: true,
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">{children}</body>
    </html>
  );
}
