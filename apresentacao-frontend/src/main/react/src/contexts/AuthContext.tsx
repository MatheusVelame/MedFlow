import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

export type UserRole = 'gestor' | 'atendente' | 'medico';

interface User {
  id: string;
  nome: string;
  email: string;
  role: UserRole;
}

interface AuthContextType {
  user: User | null;
  login: (email: string, senha: string) => Promise<boolean>;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (roles: UserRole | UserRole[]) => boolean;
  isGestor: boolean;
  isMedico: boolean;
  isAtendente: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = async (email: string, senha: string): Promise<boolean> => {
    // Mock de usuários para demonstração
    const mockUsers: User[] = [
      { id: '1', nome: 'Dr. João Silva', email: 'medico@clinica.com', role: 'medico' },
      { id: '2', nome: 'Maria Santos', email: 'atendente@clinica.com', role: 'atendente' },
      { id: '3', nome: 'Carlos Souza', email: 'gestor@clinica.com', role: 'gestor' },
    ];

    // Simulação de autenticação (todos com senha: 123456)
    if (senha === '123456') {
      const foundUser = mockUsers.find(u => u.email === email);
      if (foundUser) {
        setUser(foundUser);
        localStorage.setItem('user', JSON.stringify(foundUser));
        return true;
      }
    }
    return false;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  const hasRole = (roles: UserRole | UserRole[]): boolean => {
    if (!user) return false;
    const roleArray = Array.isArray(roles) ? roles : [roles];
    return roleArray.includes(user.role);
  };

  const isGestor = user?.role === 'gestor';
  const isMedico = user?.role === 'medico';
  const isAtendente = user?.role === 'atendente';

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      isAuthenticated: !!user,
      hasRole,
      isGestor,
      isMedico,
      isAtendente
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
}
