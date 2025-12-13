import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { EspecialidadeForm } from '@/components/EspecialidadeForm'

describe('EspecialidadeForm', () => {
  it('renders and validates required fields', async () => {
    const onSave = vi.fn()
    const onOpenChange = vi.fn()

    render(<EspecialidadeForm open={true} onOpenChange={onOpenChange} onSave={onSave} />)

    // O nome é obrigatório, tentar enviar sem nome deve mostrar mensagem
    const salvar = screen.getByRole('button', { name: /salvar/i })
    fireEvent.click(salvar)

    expect(await screen.findByText(/nome deve ter pelo menos/i)).toBeInTheDocument()
  })
})
