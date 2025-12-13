import { useState } from "react";
import {
  Calendar,
  CalendarCheck,
  Users,
  FileText,
  TestTube,
  DollarSign,
  Package,
  Home,
  Stethoscope,
  ClipboardList,
  Activity,
  CreditCard,
  Pill,
  Microscope
} from "lucide-react";
import { NavLink, useLocation } from "react-router-dom";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarTrigger,
  useSidebar,
} from "@/components/ui/sidebar";
import { useAuth, UserRole } from "@/contexts/AuthContext";

interface NavItem {
  title: string;
  url: string;
  icon: any;
  roles: UserRole[];
}

const navigationItems: NavItem[] = [
  { title: "Dashboard", url: "/", icon: Home, roles: ['gestor', 'atendente', 'medico'] },
  { title: "Agendamentos", url: "/agendamentos", icon: Calendar, roles: ['gestor', 'atendente', 'medico'] },
  { title: "Pacientes", url: "/pacientes", icon: Users, roles: ['gestor', 'atendente', 'medico'] },
  { title: "Triagem", url: "/triagem", icon: Activity, roles: ['gestor', 'atendente', 'medico'] },
  { title: "Prontuários", url: "/prontuarios", icon: FileText, roles: ['gestor', 'medico'] },
  { title: "Exames", url: "/exames", icon: TestTube, roles: ['gestor', 'medico'] },
];

const managementItems: NavItem[] = [
  { title: "Financeiro", url: "/financeiro", icon: DollarSign, roles: ['gestor', 'atendente'] },
  { title: "Faturamentos", url: "/faturamentos", icon: FileText, roles: ['gestor', 'atendente'] },
  { title: "Convênios", url: "/convenios", icon: CreditCard, roles: ['gestor', 'atendente'] },
  { title: "Estoque", url: "/estoque", icon: Package, roles: ['gestor'] },
  { title: "Medicamentos", url: "/medicamentos", icon: Pill, roles: ['gestor'] },
  { title: "Medicamentos", url: "/medicamentos-medico", icon: Pill, roles: ['medico'] },
  { title: "Consultas", url: "/consultas", icon: CalendarCheck, roles: ['gestor', 'medico'] },
  { title: "Profissionais", url: "/profissionais", icon: Stethoscope, roles: ['gestor'] },
  { title: "Especialidades", url: "/especialidades", icon: Activity, roles: ['gestor'] },
  { title: "Tipos de Exame", url: "/tipos-exames", icon: Microscope, roles: ['gestor'] },
  { title: "Relatórios", url: "/relatorios", icon: ClipboardList, roles: ['gestor'] },
];

export function MedicalSidebar() {
  const { state } = useSidebar();
  const { user, hasRole } = useAuth();
  const location = useLocation();
  const currentPath = location.pathname;
  const collapsed = state === "collapsed";

  const isActive = (path: string) => currentPath === path;
  const getNavClass = ({ isActive }: { isActive: boolean }) =>
    isActive 
      ? "bg-sidebar-accent text-sidebar-primary font-medium" 
      : "text-sidebar-foreground hover:bg-sidebar-accent/50 hover:text-sidebar-accent-foreground";

  const filterItemsByRole = (items: NavItem[]) => {
    if (!user) return [];
    return items.filter(item => item.roles.includes(user.role));
  };

  const visibleNavItems = filterItemsByRole(navigationItems);
  const visibleManagementItems = filterItemsByRole(managementItems);

  return (
    <Sidebar className={collapsed ? "w-16" : "w-64"} collapsible="icon">
      <SidebarContent>
        {/* Logo/Header */}
        <div className="p-4 border-b border-sidebar-border">
          <div className="flex items-center gap-3">
            <div className="flex-shrink-0 w-8 h-8 bg-gradient-primary rounded-lg flex items-center justify-center">
              <Stethoscope className="w-5 h-5 text-white" />
            </div>
            {!collapsed && (
              <div>
                <h2 className="text-lg font-bold text-sidebar-foreground">MedFlow</h2>
                <p className="text-sm text-sidebar-foreground/70">Sistema Médico</p>
              </div>
            )}
          </div>
        </div>

        {/* Main Navigation */}
        <SidebarGroup>
          <SidebarGroupLabel>Atendimento</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {visibleNavItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <NavLink to={item.url} end className={getNavClass}>
                      <item.icon className="w-4 h-4" />
                      {!collapsed && <span>{item.title}</span>}
                    </NavLink>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Management */}
        {visibleManagementItems.length > 0 && (
          <SidebarGroup>
            <SidebarGroupLabel>Gestão</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {visibleManagementItems.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton asChild>
                      <NavLink to={item.url} className={getNavClass}>
                        <item.icon className="w-4 h-4" />
                        {!collapsed && <span>{item.title}</span>}
                      </NavLink>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        )}

      </SidebarContent>
    </Sidebar>
  );
}