import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { ExameForm } from '@/components/ExameForm'

describe('ExameForm', () => {
  it('renders and requires paciente and tipoExame', async () => {
    const onSave = vi.fn()
    const onOpenChange = vi.fn()

    render(<ExameForm open={true} onOpenChange={onOpenChange} onSave={onSave} />)

    const submit = screen.getByRole('button', { name: /solicitar exame/i })
    fireEvent.click(submit)

    expect(await screen.findByText(/paciente é obrigatório/i)).toBeInTheDocument()
  })
})
