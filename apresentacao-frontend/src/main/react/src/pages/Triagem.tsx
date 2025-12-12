import { useState } from "react";
import { Activity, Heart, Thermometer, Weight, Ruler, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { useAuth } from "@/contexts/AuthContext";

const waitingPatients = [
  {
    id: 1,
    name: "Maria Silva",
    time: "09:00",
    priority: "normal",
    complaint: "Dor de cabeça há 2 dias",
    waitTime: "15 min"
  },
  {
    id: 2,
    name: "João Santos",
    time: "09:30",
    priority: "urgent",
    complaint: "Dor no peito",
    waitTime: "5 min"
  },
  {
    id: 3,
    name: "Ana Costa",
    time: "10:00",
    priority: "normal",
    complaint: "Febre e mal estar",
    waitTime: "0 min"
  }
];

export default function Triagem() {
  const { isGestor } = useAuth();
  const [selectedPatient, setSelectedPatient] = useState<any>(null);
  const [vitalSigns, setVitalSigns] = useState({
    bloodPressure: "",
    heartRate: "",
    temperature: "",
    weight: "",
    height: "",
    oxygenSaturation: "",
    observations: ""
  });

  const getPriorityBadge = (priority: string) => {
    return (
      <Badge 
        variant={priority === "urgent" ? "destructive" : "default"}
        className={priority === "urgent" ? "" : "bg-success text-success-foreground"}
      >
        {priority === "urgent" ? "Urgente" : "Normal"}
      </Badge>
    );
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Triagem</h1>
        <p className="text-muted-foreground">Realize a triagem e registro de sinais vitais</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Lista de Pacientes Aguardando */}
        <div className="lg:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle>Pacientes Aguardando</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {waitingPatients.map((patient) => (
                  <div 
                    key={patient.id}
                    className={`p-3 rounded-lg border cursor-pointer transition-all ${
                      selectedPatient?.id === patient.id 
                        ? "border-primary bg-primary/5" 
                        : "hover:bg-muted/50"
                    }`}
                    onClick={() => setSelectedPatient(patient)}
                  >
                    <div className="flex justify-between items-start mb-2">
                      <div>
                        <p className="font-medium">{patient.name}</p>
                        <p className="text-sm text-muted-foreground">{patient.time}</p>
                      </div>
                      {getPriorityBadge(patient.priority)}
                    </div>
                    <p className="text-sm text-muted-foreground mb-1">{patient.complaint}</p>
                    <p className="text-xs text-warning font-medium">Aguardando há {patient.waitTime}</p>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Formulário de Triagem */}
        <div className="lg:col-span-2">
          {selectedPatient ? (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Activity className="w-5 h-5" />
                  Triagem - {selectedPatient.name}
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Queixa Principal */}
                <div>
                  <Label className="text-base font-medium">Queixa Principal</Label>
                  <p className="text-sm text-muted-foreground mt-1 p-3 bg-muted/50 rounded-lg">
                    {selectedPatient.complaint}
                  </p>
                </div>

                {/* Sinais Vitais */}
                <div>
                  <Label className="text-base font-medium mb-4 block">Sinais Vitais</Label>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    <div>
                      <Label htmlFor="bp">Pressão Arterial</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Heart className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="bp"
                          placeholder="120/80"
                          value={vitalSigns.bloodPressure}
                          onChange={(e) => setVitalSigns(prev => ({...prev, bloodPressure: e.target.value}))}
                        />
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="hr">Frequência Cardíaca</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Activity className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="hr"
                          placeholder="72 bpm"
                          value={vitalSigns.heartRate}
                          onChange={(e) => setVitalSigns(prev => ({...prev, heartRate: e.target.value}))}
                        />
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="temp">Temperatura</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Thermometer className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="temp"
                          placeholder="36.5°C"
                          value={vitalSigns.temperature}
                          onChange={(e) => setVitalSigns(prev => ({...prev, temperature: e.target.value}))}
                        />
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="weight">Peso</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Weight className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="weight"
                          placeholder="70 kg"
                          value={vitalSigns.weight}
                          onChange={(e) => setVitalSigns(prev => ({...prev, weight: e.target.value}))}
                        />
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="height">Altura</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Ruler className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="height"
                          placeholder="1.70 m"
                          value={vitalSigns.height}
                          onChange={(e) => setVitalSigns(prev => ({...prev, height: e.target.value}))}
                        />
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="spo2">SpO2</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Activity className="w-4 h-4 text-muted-foreground" />
                        <Input
                          id="spo2"
                          placeholder="98%"
                          value={vitalSigns.oxygenSaturation}
                          onChange={(e) => setVitalSigns(prev => ({...prev, oxygenSaturation: e.target.value}))}
                        />
                      </div>
                    </div>
                  </div>
                </div>

                {/* Observações */}
                <div>
                  <Label htmlFor="obs">Observações Gerais</Label>
                  <Textarea
                    id="obs"
                    placeholder="Observações sobre o estado geral do paciente..."
                    className="mt-1"
                    value={vitalSigns.observations}
                    onChange={(e) => setVitalSigns(prev => ({...prev, observations: e.target.value}))}
                  />
                </div>

                {/* Ações */}
                {!isGestor && (
                  <div className="flex gap-3 pt-4">
                    <Button className="bg-gradient-primary hover:bg-primary-hover">
                      Finalizar Triagem
                    </Button>
                    <Button variant="outline">
                      Salvar Rascunho
                    </Button>
                    <Button variant="outline" className="ml-auto">
                      Cancelar
                    </Button>
                  </div>
                )}
                {isGestor && (
                  <div className="p-4 bg-muted/50 rounded-lg text-center">
                    <p className="text-sm text-muted-foreground">
                      Modo visualização - Apenas profissionais de saúde podem realizar triagens
                    </p>
                  </div>
                )}
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <Activity className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-medium mb-2">Selecione um Paciente</h3>
                <p className="text-muted-foreground">
                  Escolha um paciente da lista para iniciar a triagem
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}