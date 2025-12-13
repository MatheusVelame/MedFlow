import { ReactNode } from "react";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { MedicalSidebar } from "./MedicalSidebar";
import { MedicalHeader } from "./MedicalHeader";

interface MedicalLayoutProps {
  children: ReactNode;
}

export function MedicalLayout({ children }: MedicalLayoutProps) {
  return (
    <SidebarProvider>
      <div className="min-h-screen flex w-full bg-background">
        <MedicalSidebar />
        <div className="flex-1 flex flex-col">
          <MedicalHeader />
          <main className="flex-1 p-6 overflow-auto">
            {children}
          </main>
        </div>
      </div>
    </SidebarProvider>
  );
}